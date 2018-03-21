package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.*;
import com.adiaz.madrid.entities.*;
import com.adiaz.madrid.utils.ClassificationLineEntity;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.adiaz.madrid.utils.MatchLineEntity;
import com.google.appengine.tools.cloudstorage.*;
import com.googlecode.objectify.Ref;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service ("ReleaseManager")
public class ReleaseManagerImpl implements ReleaseManager {

    private static final Logger logger = Logger.getLogger(ReleaseManagerImpl.class);

    private static final String BUCKET_CLASSIFICATION = "deportes_madrid_classification";
    private static final String BUCKET_MATCHES= "deportes_madrid_matches";

    /** Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB */
    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * This is where backoff parameters are configured. Here it is aggressively retrying with
     * backoff, up to 10 times but taking no more that 15 seconds total to do so.
     */
    private static final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());

    private static final int INSERT_BLOCK_SIZE = 200;
    private static final int LINES_BLOCK_SIZE = 500;

    @Autowired
    ReleaseMatchesDAO releaseMatchesDAO;

    @Autowired
    ReleaseClassificationDAO releaseClassificationDAO;

    @Autowired
    TeamDAO teamDAO;

    @Autowired
    PlaceDAO placeDAO;

    @Autowired
    CompetitionDAO competitionDAO;

    @Autowired
    MatchDAO matchDAO;

    @Autowired
    ClassificationDAO classificationDAO;

    @Override
    public void removeReleaseMatches(String id) throws Exception {
        releaseMatchesDAO.remove(id);
    }

    @Override
    public void removeReleaseClassification(String id) throws Exception {
        releaseClassificationDAO.remove(id);
    }

    @Override
    public List<ReleaseMatches> queryAllReleaseMatches() {
        return releaseMatchesDAO.findAll();
    }

    @Override
    public ReleaseClassification queryReleaseClassifications(String id) {
        return releaseClassificationDAO.findById(id);
    }

    @Override
    public List<ReleaseClassification> queryAllReleaseClassifications() {
        return releaseClassificationDAO.findAll();
    }

    @Override
    public ReleaseMatches createOrGetLastReleasePublishedMatches() throws Exception {
        String lastReleasePublished = DeportesMadridUtils.getLastReleasePublished(DeportesMadridConstants.URL_MATCHES);
        ReleaseMatches release = releaseMatchesDAO.findById(lastReleasePublished);
        if (release==null) {
            release = new ReleaseMatches();
            String urlMatchesStr = DeportesMadridUtils.getLastReleasePublishedUrl(DeportesMadridConstants.URL_MATCHES);
            release.setId(lastReleasePublished);
            release.setPublishUrl(urlMatchesStr);
            release.setUpdatedBucket(false);
            release.setUpdatedTeams(false);
            release.setUpdatedPlaces(false);
            release.setUpdatedMatches(false);
            release.setUpdatedCompetitions(false);
            release.setLines(0);
            release.setLinesTeams(0);
            release.setLinesPlaces(0);
            release.setLinesCompetitions(0);
            release.setLinesMatches(0);
            release.setLinesMatches(0);
            releaseMatchesDAO.create(release);
        }
        return release;
    }

    @Override
    public ReleaseClassification createOrGetLastReleasePublishedClassification() throws Exception {
        String lastReleasePublished = DeportesMadridUtils.getLastReleasePublished(DeportesMadridConstants.URL_CLASSIFICATION);
        ReleaseClassification release = releaseClassificationDAO.findById(lastReleasePublished);
        if (release==null) {
            release = new ReleaseClassification();
            String urlMatchesStr = DeportesMadridUtils.getLastReleasePublishedUrl(DeportesMadridConstants.URL_CLASSIFICATION);
            release.setId(lastReleasePublished);
            release.setPublishUrl(urlMatchesStr);
            release.setUpdatedBucket(false);
            release.setUpdatedClassification(false);
            release.setLines(0);
            release.setLinesClassification(0);
            releaseClassificationDAO.create(release);
        }
        return release;
    }

    @Override
    public ReleaseMatches queryReleaseMatches(String id) {
        return releaseMatchesDAO.findById(id);
    }

    @Override
    public void loadBucketMatches(String idRelease) throws Exception {
        ReleaseMatches releaseMatches = releaseMatchesDAO.findById(idRelease);
        copyObjectInBucket(DeportesMadridConstants.URL_MATCHES, BUCKET_MATCHES, idRelease + ".csv");
        releaseMatches.setUpdatedBucket(true);
        releaseMatches.setLines(countLines(idRelease, BUCKET_MATCHES));
        releaseMatchesDAO.update(releaseMatches);
    }

    @Override
    public void loadBucketClassification(String idRelease) throws Exception {
        ReleaseClassification releaseClassification = releaseClassificationDAO.findById(idRelease);
        copyObjectInBucket(DeportesMadridConstants.URL_CLASSIFICATION, BUCKET_CLASSIFICATION, idRelease + ".csv");
        releaseClassification.setUpdatedBucket(true);
        releaseClassification.setLines(countLines(idRelease, BUCKET_CLASSIFICATION));
        releaseClassificationDAO.update(releaseClassification);
    }

    @Override
    public void updateTeams(String idRelease) throws Exception {
        ReleaseMatches releaseMatches = releaseMatchesDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, BUCKET_MATCHES);
        scanner.nextLine();
        int linesCount = 1;
        Map<Long, Team> teamsMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            MatchLineEntity lineEntity = new MatchLineEntity(line);
            Team teamLocal = addOrUpdateTeam(lineEntity.getField06_codEquipoLocal(), lineEntity.getField22_equipoLocal());
            Team teamVisitor = addOrUpdateTeam(lineEntity.getField07_codEquipoVisitante(), lineEntity.getField23_equipoVisitante());
            if (teamLocal!=null) {
                teamsMap.put(teamLocal.getId(), teamLocal);
            }
            if (teamVisitor!=null) {
                teamsMap.put(teamVisitor.getId(), teamVisitor);
            }
            linesCount++;
            if (teamsMap.size() % INSERT_BLOCK_SIZE == 0) {
                teamDAO.insertList(teamsMap.values());
                teamsMap = new HashMap<>();
            }
            if (linesCount % LINES_BLOCK_SIZE == 0) {
                logger.debug("updateTeams linesCount ->" + linesCount);
                releaseMatches.setLinesTeams(linesCount);
                releaseMatchesDAO.update(releaseMatches);
            }
        }
        scanner.close();
        teamDAO.insertList(teamsMap.values());
        releaseMatches.setLinesTeams(linesCount);
        releaseMatches.setUpdatedTeams(true);
        releaseMatchesDAO.update(releaseMatches);
        logger.debug("last updateTeams linesCount ->" + linesCount);
    }

    @Override
    public void updatePlaces(String idRelease) throws Exception {
        ReleaseMatches releaseMatches = releaseMatchesDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, BUCKET_MATCHES);
        scanner.nextLine();
        Map<Long, Place> placesMap = new HashMap<>();
        Integer linesCount = 1;
        while (scanner.hasNextLine()) {
            linesCount++;
            String line = scanner.nextLine();
            MatchLineEntity matchLineEntity = new MatchLineEntity(line);
            Place place = new Place();
            Long id = matchLineEntity.getField10_codCampo();
            if (id!=0) {
                Place placeOriginal = placeDAO.findById(id);
                place.setId(id);
                place.setName(matchLineEntity.getField24_campo());
                place.setCoordX(matchLineEntity.getField29_coordx());
                place.setCoordY(matchLineEntity.getField30_coordy());
                if (placeOriginal==null || !placeOriginal.equals(place)) {
                    placesMap.put(place.getId(), place);
                }
            }
            if (placesMap.size() % INSERT_BLOCK_SIZE == 0) {
                placeDAO.insertList(placesMap.values());
                placesMap = new HashMap<>();
            }
            if (linesCount % LINES_BLOCK_SIZE == 0) {
                logger.debug("update places --> " +linesCount );
                releaseMatches.setLinesPlaces(linesCount);
                releaseMatchesDAO.update(releaseMatches);
            }
        }
        scanner.close();
        placeDAO.insertList(placesMap.values());
        releaseMatches.setLinesPlaces(linesCount);
        releaseMatches.setUpdatedPlaces(true);
        releaseMatchesDAO.update(releaseMatches);
        logger.debug("last update places --> " +linesCount );

    }

    @Override
    public void updateCompetitions(String idRelease) throws Exception {
        ReleaseMatches releaseMatches = releaseMatchesDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, BUCKET_MATCHES);
        scanner.nextLine();
        Map<String, Competition> competitionMap = new HashMap<>();
        Integer linesCount = 1;
        while (scanner.hasNextLine()) {
            linesCount++;
            String line = scanner.nextLine();
            MatchLineEntity matchLineEntity = new MatchLineEntity(line);
            Competition competition = new Competition();
            Integer codTemporada = matchLineEntity.getField00_codTemporada();
            String codCompeticion = matchLineEntity.getField01_codCompeticion();
            Integer codFase = matchLineEntity.getField02_codFase();
            Integer codGrupo = matchLineEntity.getField03_codGrupo();
            String idCompetition = DeportesMadridUtils.generateIdCompetition(codTemporada, codCompeticion, codFase, codGrupo);
            Competition competitionOriginal = competitionDAO.findById(idCompetition);
            competition.setCodTemporada(codTemporada);
            competition.setCodCompeticion( codCompeticion);
            competition.setCodFase(codFase);
            competition.setCodGrupo(codGrupo);
            /* generate the id from the temporada, competicion, fase, grupo. */
            competition.setId(idCompetition);
            competition.setNombreTemporada(matchLineEntity.getField15_nombreTemporada());
            competition.setNombreCompeticion(matchLineEntity.getField16_nombreCompeticion());
            competition.setNombreFase(matchLineEntity.getField17_nombreFase());
            competition.setNombreGrupo(matchLineEntity.getField18_nombreGrupo());
            competition.setDeporte(matchLineEntity.getField19_nombreDeporte());
            competition.setDistrito(matchLineEntity.getField26_distrito());
            if (competitionOriginal==null || !competitionOriginal.equals(competition)) {
                competitionMap.put(competition.getId(), competition);
            }
            if (competitionMap.size() % INSERT_BLOCK_SIZE == 0) {
                competitionDAO.insertList(competitionMap.values());
                competitionMap = new HashMap<>();
            }
            if (linesCount % LINES_BLOCK_SIZE == 0) {
                logger.debug("update competitions --> " + linesCount );
                releaseMatches.setLinesCompetitions(linesCount);
                releaseMatchesDAO.update(releaseMatches);
            }
        }
        scanner.close();
        competitionDAO.insertList(competitionMap.values());
        releaseMatches.setLinesCompetitions(linesCount);
        releaseMatches.setUpdatedCompetitions(true);
        releaseMatchesDAO.update(releaseMatches);
        logger.debug("last update competitions --> " + linesCount );
    }

    @Override
    public void updateMatches(String idRelease) throws Exception {
        ReleaseMatches releaseMatches = releaseMatchesDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, BUCKET_MATCHES);
        scanner.nextLine();
        Map<String, Match> matchMap = new HashMap<>();
        Integer linesCount = 1;
        while (scanner.hasNextLine()) {
            linesCount++;
            String line = scanner.nextLine();
            MatchLineEntity matchLineEntity = new MatchLineEntity(line);
            Match match = new Match();
            //find competition
            Integer codTemporada = matchLineEntity.getField00_codTemporada();
            String codCompeticion = matchLineEntity.getField01_codCompeticion();
            Integer codFase = matchLineEntity.getField02_codFase();
            Integer codGrupo = matchLineEntity.getField03_codGrupo();
            Integer weekNumber = matchLineEntity.getField04_weekNum();
            Integer matchNumber = matchLineEntity.getField05_matchNum();
            String idCompetition = DeportesMadridUtils.generateIdCompetition(codTemporada, codCompeticion, codFase, codGrupo);
            String idMatch = DeportesMadridUtils.generateIdMatch(codTemporada, codCompeticion, codFase, codGrupo, weekNumber, matchNumber);
            Match matchOriginal = matchDAO.findById(idMatch);
            match.setId(idMatch);
            Competition competition = competitionDAO.findCompetition(idCompetition);
            match.setCompetition(competition);
            match.setCompetitionRef(Ref.create(competition));
            //find localteam
            if (matchLineEntity.getField06_codEquipoLocal()!=0) {
                Team teamLocal = teamDAO.findById(matchLineEntity.getField06_codEquipoLocal());
                match.setTeamLocal(teamLocal);
                match.setTeamLocalRef(Ref.create(teamLocal));
            }
            //find visitorteam
            if (matchLineEntity.getField07_codEquipoVisitante()!=0) {
                Team teamVisitor = teamDAO.findById(matchLineEntity.getField07_codEquipoVisitante());
                match.setTeamVisitor(teamVisitor);
                match.setTeamVisitorRef(Ref.create(teamVisitor));
            }
            //find place
            if (matchLineEntity.getField10_codCampo()!=0) {
                Place place = placeDAO.findById(matchLineEntity.getField10_codCampo());
                match.setPlace(place);
                match.setPlaceRef(Ref.create(place));
            }
            match.setScoreLocal(matchLineEntity.getField08_scoreLocal());
            match.setScoreVisitor(matchLineEntity.getField09_scoreVisitor());
            String dateStr = matchLineEntity.getField11_fecha() + " " + matchLineEntity.getField12_hora();
            match.setDate(DeportesMadridUtils.formatMatchDate(dateStr));
            match.setWeekNum(matchLineEntity.getField04_weekNum());
            match.setMatchNum(matchLineEntity.getField05_matchNum());
            match.setScheduled(matchLineEntity.getField13_programado()==1);
            match.setState(DeportesMadridConstants.MATCH_STATE.PENDIENTE.getValue());
            if (StringUtils.isNotEmpty(matchLineEntity.getField14_estado())) {
                match.setState(DeportesMadridConstants.MATCH_STATE.createState(matchLineEntity.getField14_estado().charAt(0)).getValue());
            }
            if (matchOriginal==null || !matchOriginal.equals(match)) {
                matchMap.put(match.getId(), match);
            }
            if (matchMap.size() % INSERT_BLOCK_SIZE == 0) {
                matchDAO.insertList(matchMap.values());
                matchMap = new HashMap<>();
            }
            if (linesCount % LINES_BLOCK_SIZE == 0) {
                logger.debug("update matches --> " + linesCount );
                releaseMatches.setLinesMatches(linesCount);
                releaseMatchesDAO.update(releaseMatches);
            }
        }
        scanner.close();
        matchDAO.insertList(matchMap.values());
        releaseMatches.setLinesMatches(linesCount);
        releaseMatches.setUpdatedMatches(true);
        releaseMatchesDAO.update(releaseMatches);
        logger.debug("last update matches --> " + linesCount );

    }

    @Override
    public void updateClassifications(String idRelease) throws Exception {
        ReleaseClassification releaseClassification = releaseClassificationDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, BUCKET_CLASSIFICATION);
        scanner.nextLine();
        int linesCount = 1;
        Map<String, ClassificationEntry> classificationEntryMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            linesCount++;
            String line = scanner.nextLine();
            ClassificationLineEntity classificationLineEntity = new ClassificationLineEntity(line);
            try {
                Integer codTemporada = classificationLineEntity.getField00_codTemporada();
                String codCompeticion = classificationLineEntity.getField01_codCompeticion();
                Integer codFase = classificationLineEntity.getField02_codFase();
                Integer codGrupo = classificationLineEntity.getField03_codGrupo();
                Integer codEquipo = classificationLineEntity.getField04_codEquipo();
                String idCompetition = DeportesMadridUtils.generateIdCompetition(codTemporada, codCompeticion, codFase, codGrupo);
                String idClassificationEntry = DeportesMadridUtils.generateIdClassification(codTemporada, codCompeticion, codFase, codGrupo, codEquipo);
                Competition competition = competitionDAO.findCompetition(idCompetition);
                ClassificationEntry classificationEntryOriginal = classificationDAO.findById(idClassificationEntry);
                if (competition!=null) {
                    Ref<Competition> competitionRef = Ref.create(competition);
                    Team team = teamDAO.findById(codEquipo.longValue());
                    Ref<Team> teamRef = Ref.create(team);
                    ClassificationEntry classificationEntry = new ClassificationEntry();
                    classificationEntry.setId(idClassificationEntry);
                    classificationEntry.setCompetitionRef(competitionRef);
                    classificationEntry.setTeamRef(teamRef);
                    classificationEntry.setTeam(team);
                    classificationEntry.setPosition(classificationLineEntity.getField05_posicion());
                    classificationEntry.setPoints(classificationLineEntity.getField06_puntos());
                    classificationEntry.setMatchesPlayed(classificationLineEntity.getField07_partidosJugados());
                    classificationEntry.setMatchesWon(classificationLineEntity.getField08_partidosGanados());
                    classificationEntry.setMatchesDrawn(classificationLineEntity.getField09_partidosEmpatados());
                    classificationEntry.setMatchesLost(classificationLineEntity.getField10_partidosPerdidos());
                    classificationEntry.setPointsFavor(classificationLineEntity.getField11_golesFavor());
                    classificationEntry.setPointsAgainst(classificationLineEntity.getField12_golesContra());
                    if (classificationEntryOriginal==null || !classificationEntry.equals(classificationEntryOriginal)) {
                        classificationEntryMap.put(idClassificationEntry, classificationEntry);
                    }
                }
                if (linesCount%LINES_BLOCK_SIZE==0) {
                    logger.debug("update classifications --> " + linesCount );
                    releaseClassification.setLinesClassification(linesCount);
                    releaseClassificationDAO.update(releaseClassification);
                }
                if (classificationEntryMap.size()==INSERT_BLOCK_SIZE) {
                    classificationDAO.insertList(classificationEntryMap.values());
                    classificationEntryMap = new HashMap<>();
                }
            } catch (Exception e) {
                logger.error("error when generating classificationLineEntity " + classificationLineEntity, e);
            }
        }
        scanner.close();
        classificationDAO.insertList(classificationEntryMap.values());
        logger.debug("last update classifications --> " + linesCount );
        releaseClassification.setLinesClassification(linesCount);
        releaseClassification.setUpdatedClassification(true);
        releaseClassificationDAO.update(releaseClassification);
    }

    @Override
    public void updateDataStore() throws Exception {
        ReleaseMatches releaseMatches = createOrGetLastReleasePublishedMatches();
        if (!releaseMatches.getUpdatedBucket()) {
            loadBucketMatches(releaseMatches.getId());
        }
        if (!releaseMatches.getUpdatedTeams()) {
            updateTeams(releaseMatches.getId());
        }
        if (!releaseMatches.getUpdatedPlaces()) {
            updatePlaces(releaseMatches.getId());
        }
        if (!releaseMatches.getUpdatedCompetitions()) {
            updateCompetitions(releaseMatches.getId());
        }
        if (!releaseMatches.getUpdatedMatches()) {
            updateMatches(releaseMatches.getId());
        }
        ReleaseClassification releaseClassification = createOrGetLastReleasePublishedClassification();
        if (!releaseClassification.getUpdatedBucket()) {
            loadBucketClassification(releaseClassification.getId());
        }
        if (!releaseClassification.getUpdatedClassification()) {
            updateClassifications(releaseClassification.getId());
        }
    }

    private Scanner getScanner(String releaseId, String bucket) {
        GcsFilename gcsFilename = new GcsFilename(bucket, releaseId + ".csv");
        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(gcsFilename, 0, BUFFER_SIZE);
        InputStream inputStream = Channels.newInputStream(readChannel);
        return new Scanner(inputStream, StandardCharsets.ISO_8859_1.name());
    }

    private void copyObjectInBucket(String urlInput, String bucketName, String objectName) throws IOException {
        GcsFilename gcsFilename = new GcsFilename(bucketName, objectName);
        GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
        GcsOutputChannel outputChannel;
        outputChannel = gcsService.createOrReplace(gcsFilename, instance);
        URL url = new URL(urlInput);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(30000);
        urlConnection.setReadTimeout(30000);
        int contentLength = urlConnection.getContentLength();
        logger.debug(bucketName + " contentLength: " + contentLength);
        InputStream inputStream = urlConnection.getInputStream();
        copy(inputStream, Channels.newOutputStream(outputChannel));
    }

    /** Transfer the data from the inputStream to the outputStream. Then close both streams. */
    private void copy(InputStream input, OutputStream output) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                logger.debug("bytesRead " + bytesRead);
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    private Integer countLines(String releaseId, String bucketName) throws Exception {
        Scanner scanner = getScanner(releaseId, bucketName);
        Integer linesCount = 0;
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            linesCount++;
        }
        scanner.close();
        return linesCount;
    }

    private Team addOrUpdateTeam(Long idTeam, String nameTeam) throws Exception {
        if (idTeam!=null && idTeam!=0 && StringUtils.isNotBlank(nameTeam)) {
            Team teamNew = new Team();
            teamNew.setId(idTeam);
            teamNew.setName(nameTeam);
            Team teamOriginal = teamDAO.findById(idTeam);
            if (teamOriginal==null && !teamOriginal.equals(teamNew)) {
                return teamNew;
            }
        }
        return null;
    }
}