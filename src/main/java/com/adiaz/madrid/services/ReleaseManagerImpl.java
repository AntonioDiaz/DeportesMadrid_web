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

    static final Logger logger = Logger.getLogger(ReleaseManagerImpl.class);

    private static final String BUCKET_CLASSIFICATION = "deportes_madrid_classification";
    private static final String BUCKET_MATCHES= "deportes_madrid_matches";

    /** Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB */
    private static final int BUFFER_SIZE = 1 * 1024 * 1024;
    /**
     * This is where backoff parameters are configured. Here it is aggressively retrying with
     * backoff, up to 10 times but taking no more that 15 seconds total to do so.
     */
    private static final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());

    @Autowired
    ReleaseDAO releaseDAO;

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
    public void addRelease(Release release) throws Exception {
        releaseDAO.create(release);
    }

    @Override
    public boolean updateRelease(Release release) throws Exception {
        return releaseDAO.update(release);
    }

    @Override
    public void removeRelease(Long id) throws Exception {
        releaseDAO.remove(id);
    }

    @Override
    public List<Release> queryAllRelease() {
        List<Release> allRelease = releaseDAO.findAll();
        allRelease.sort(new Comparator<Release>() {
            @Override
            public int compare(Release o1, Release o2) {
                return o2.getPublishDateStr().compareTo(o1.getPublishDateStr());
            }
        });
        return allRelease;
    }

    @Override
    public Release lastRelease() {
        Release lastRelease = null;
        for (Release release : queryAllRelease()) {
            if (lastRelease==null || release.getPublishDateStr().compareTo(lastRelease.getPublishDateStr())>0) {
                lastRelease = release;
            }
        }
        return lastRelease;
    }

    @Override
    public void createEmptyRelease() throws Exception {
        Release release = new Release();
        String urlMatchesStr = DeportesMadridUtils.getLastReleasePublishedUrlMatches();
        String urlClassificationStr = DeportesMadridUtils.getLastReleasePublishedUrlClassification();
        String lastPublished = DeportesMadridUtils.getLastReleasePublished();
        release.setPublishUrlMatches(urlMatchesStr);
        release.setPublishUrlClassification(urlClassificationStr);
        release.setPublishDateStr(lastPublished);
        release.setUpdatedBucket(false);
        release.setUpdatedTeams(false);
        release.setUpdatedPlaces(false);
        release.setUpdatedClassification(false);
        release.setUpdatedMatches(false);
        release.setUpdateCompetitions(false);
        releaseDAO.create(release);
    }

    @Override
    public void loadBucket(Long idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        String lastPublished = release.getPublishDateStr();
        copyObjectInBucket(DeportesMadridConstants.URL_CLASSIFICATION, BUCKET_CLASSIFICATION, lastPublished + ".csv");
        copyObjectInBucket(DeportesMadridConstants.URL_MATCHES, BUCKET_MATCHES, lastPublished + ".csv");
        release.setUpdatedBucket(true);
        releaseDAO.update(release);
    }

    @Override
    public void updateTeams(Long idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        Scanner scanner = getScanner(release.getPublishDateStr(), BUCKET_MATCHES);
        scanner.nextLine();
        int cont = 0;
        Map<Long, Team> teamMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            cont++;
            String line = scanner.nextLine();
            MatchLineEntity matchLineEntity = new MatchLineEntity(line);
            if (matchLineEntity!=null ) {
                if (matchLineEntity.getField06_codEquipoLocal()!=0) {
                    Team team = addOrUpdateTeam(matchLineEntity.getField06_codEquipoLocal(), matchLineEntity.getField22_equipoLocal());
                    if (team!=null && !teamMap.containsKey(team.getId())) {
                        teamMap.put(team.getId(), team);
                    }
                }
                if (matchLineEntity.getField07_codEquipoVisitante()!=0) {
                    Team team = addOrUpdateTeam(matchLineEntity.getField07_codEquipoVisitante(), matchLineEntity.getField23_equipoVisitante());
                    if (team!=null && !teamMap.containsKey(team.getId())) {
                        teamMap.put(team.getId(), team);
                    }
                }
            }
            if (teamMap.size()==100) {
                cont += teamMap.size();
                teamDAO.insertList(teamMap.values());
                teamMap = new HashMap<>();
                logger.debug("save teams" + cont);
            }
        }
        teamDAO.insertList(teamMap.values());
        cont += teamMap.size();
        logger.debug("last save teams " + cont);
        scanner.close();
        release.setUpdatedTeams(true);
        releaseDAO.update(release);
    }

    @Override
    public void updatePlaces(Long idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        release.setUpdatedPlaces(true);
        readPlaces(release.getPublishDateStr());
        releaseDAO.update(release);
    }

    @Override
    public void updateCompetitions(Long idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        release.setUpdateCompetitions(true);
        readCompetitions(release.getPublishDateStr());
        releaseDAO.update(release);
    }

    @Override
    public void updateMatches(Long idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        release.setUpdatedMatches(true);
        readMatches(release.getPublishDateStr());
        releaseDAO.update(release);
    }

    @Override
    public void updateClassifications(Long idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        release.setUpdatedClassification(true);
        readClassification(release.getPublishDateStr());
        releaseDAO.update(release);
    }

    private void readClassification(String publishDateStr) throws Exception {
        Scanner scanner = getScanner(publishDateStr, BUCKET_CLASSIFICATION);
        scanner.nextLine();
        int linesCont = 0;
        Map<String, ClassificationEntry> classificationEntryMap = new HashMap<>();
        while (scanner.hasNextLine()) {
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
                Ref<Competition> competitionRef = Ref.create(competition);
                Team team = teamDAO.findById(codEquipo.longValue());
                Ref<Team> teamRef = Ref.create(team);
                ClassificationEntry classificationEntry = new ClassificationEntry();
                classificationEntry.setId(idClassificationEntry);
                classificationEntry.setCompetitionRef(competitionRef);
                classificationEntry.setTeamRef(teamRef);
                classificationEntry.setPosition(classificationLineEntity.getField05_posicion());
                classificationEntry.setPoints(classificationLineEntity.getField06_puntos());
                classificationEntry.setMatchesPlayed(classificationLineEntity.getField07_partidosJugados());
                classificationEntry.setMatchesWon(classificationLineEntity.getField08_partidosGanados());
                classificationEntry.setMatchesDrawn(classificationLineEntity.getField09_partidosEmpatados());
                classificationEntry.setMatchesLost(classificationLineEntity.getField10_partidosPerdidos());
                classificationEntry.setPointsFavor(classificationLineEntity.getField11_golesFavor());
                classificationEntry.setPointsAgainst(classificationLineEntity.getField12_golesContra());
                classificationDAO.create(classificationEntry);
                linesCont++;
                if (!classificationEntryMap.containsKey(idClassificationEntry)) {
                    classificationEntryMap.put(idClassificationEntry, classificationEntry);
                }
                if (classificationEntryMap.size()==100) {
                    linesCont += classificationEntryMap.size();
                    classificationDAO.create(classificationEntryMap.values());
                    classificationEntryMap = new HashMap<>();
                    logger.debug("classification linesCont " + linesCont);
                }
            } catch (Exception e) {
                logger.error("error when generating the match \n classificationLineEntity " + classificationLineEntity, e);
            }
        }
        classificationDAO.create(classificationEntryMap.values());
        linesCont += classificationEntryMap.size();
        logger.debug("last: classification linesCont " + linesCont);

    }

    private void readMatches(String publishDateStr) throws Exception {
        Scanner scanner = getScanner(publishDateStr, BUCKET_MATCHES);
        scanner.nextLine();
        int matchesInserted = 0;
        Map<String, Match> matchMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            MatchLineEntity matchLineEntity = new MatchLineEntity(line);
            //logger.debug(line);
            if (matchLineEntity!=null ) {
                try {
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
                    match.setId(idMatch);
                    Competition competition = competitionDAO.findCompetition(idCompetition);
                    match.setCompetitionRef(Ref.create(competition));
                    //find localteam
                    if (matchLineEntity.getField06_codEquipoLocal()!=0) {
                        Team teamLocal = teamDAO.findById(matchLineEntity.getField06_codEquipoLocal());
                        match.setTeamLocalRef(Ref.create(teamLocal));
                    }
                    //find visitorteam
                    if (matchLineEntity.getField07_codEquipoVisitante()!=0) {
                        //logger.debug("codVisitante " + matchLineEntity.getField07_codEquipoVisitante());
                        Team teamVisitor = teamDAO.findById(matchLineEntity.getField07_codEquipoVisitante());
                        match.setTeamVisitorRef(Ref.create(teamVisitor));
                    }
                    //find place
                    if (matchLineEntity.getField10_codCampo()!=0) {
                        Place place = placeDAO.findById(matchLineEntity.getField10_codCampo());
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
                    if (!matchMap.containsKey(idMatch)) {
                        matchMap.put(idMatch, match);
                    }
                    if (matchMap.size()==100) {
                        matchDAO.create(matchMap.values());
                        matchesInserted+=matchMap.values().size();
                        logger.debug("Matches inserted " + matchesInserted);
                        matchMap = new HashMap<>();
                    }
                } catch (Exception e) {
                    logger.error("error when generating the match \n matchLineEntity " + matchLineEntity, e);
                }
            }
        }
        matchDAO.create(matchMap.values());
        matchesInserted+=matchMap.values().size();
        logger.debug("Matches inserted " + matchesInserted);
        scanner.close();
    }

    private Scanner getScanner(String publishDateStr, String bucket) {
        GcsFilename gcsFilename = new GcsFilename(bucket, publishDateStr + ".csv");
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

    private void readPlaces(String publishDateStr) throws Exception {
        Scanner scanner = getScanner(publishDateStr, BUCKET_MATCHES);
        scanner.nextLine();
        Map<Long, Place> placesMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            MatchLineEntity matchLineEntity = new MatchLineEntity(line);
            if (matchLineEntity!=null ) {
                Place place = new Place();
                place.setId(matchLineEntity.getField10_codCampo());
                place.setName(matchLineEntity.getField24_campo());
                place.setCoordX(matchLineEntity.getField29_coordx());
                place.setCoordY(matchLineEntity.getField30_coordy());
                if (place.getId()!=0 && !placesMap.containsKey(place.getId())) {
                    placesMap.put(place.getId(), place);
                }
            }
        }
        scanner.close();
        placeDAO.create(placesMap.values());
    }

    // TODO: 16/2/18 this should be in team manager.
    private Team addOrUpdateTeam(Long idTeam, String nameTeam) throws Exception {
        Team team = null;
        if (idTeam!=null && StringUtils.isNotBlank(nameTeam)) {
            team = new Team();
            team.setId(idTeam);
            team.setName(nameTeam);
        }
        return team;
    }

    private void readCompetitions(String publishDateStr) throws Exception {
        Scanner scanner = getScanner(publishDateStr, BUCKET_MATCHES);
        scanner.nextLine();
        Map<String, Competition> competitionMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            MatchLineEntity matchLineEntity = new MatchLineEntity(line);
            if (matchLineEntity!=null) {
                Competition competition = new Competition();
                competition.setCodTemporada(matchLineEntity.getField00_codTemporada());
                competition.setCodCompeticion( matchLineEntity.getField01_codCompeticion());
                competition.setCodFase(matchLineEntity.getField02_codFase());
                competition.setCodGrupo(matchLineEntity.getField03_codGrupo());
                /* generate the id from the temporada, competicion, fase, grupo. */
                competition.setId(competition.generateId());
                competition.setNombreTemporada(matchLineEntity.getField15_nombreTemporada());
                competition.setNombreCompeticion(matchLineEntity.getField16_nombreCompeticion());
                competition.setNombreFase(matchLineEntity.getField17_nombreFase());
                competition.setNombreGrupo(matchLineEntity.getField18_nombreGrupo());
                competition.setDeporte(matchLineEntity.getField19_nombreDeporte());
                competition.setDistrito(matchLineEntity.getField26_distrito());
                if (!competitionMap.containsKey(competition.getId())) {
                    competitionMap.put(competition.getId(), competition);
                }
            }
            if (competitionMap.size()==100) {
                competitionDAO.createCompetitions(competitionMap.values());
                competitionMap = new HashMap<>();
            }
        }
        competitionDAO.createCompetitions(competitionMap.values());
        scanner.close();
    }

}
