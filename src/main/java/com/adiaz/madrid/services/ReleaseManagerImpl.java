package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.*;
import com.adiaz.madrid.entities.*;
import com.adiaz.madrid.utils.ClassificationLineEntity;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.adiaz.madrid.utils.MatchLineEntity;
import com.google.appengine.tools.cloudstorage.*;
import org.apache.commons.codec.digest.DigestUtils;
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

    private static final int INSERT_BLOCK_SIZE = 300;
    private static final int LINES_BLOCK_SIZE = 500;

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

    @Autowired
    ReleaseDAO releaseDAO;

    @Override
    public List<Release> queryAllRelease() {
        return releaseDAO.findAll();
    }

    @Override
    public Release queryLastRelease() {
        Release lastRelease = null;
        for (Release release : releaseDAO.findAll()) {
            if (lastRelease==null || lastRelease.getId().compareTo(release.getId())<0) {
                lastRelease = release;
            }
        }
        return lastRelease;
    }

    @Override
    public void updateTeams(String idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, DeportesMadridConstants.BUCKET_MATCHES);
        scanner.nextLine();
        int linesCount = 1;
        Map<Long, Team> teamsMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            linesCount++;
            if (release.getLinesTeams()<=linesCount) {
                MatchLineEntity lineEntity = new MatchLineEntity(line);
                Team team = addOrUpdateTeam(lineEntity.getField06_codEquipoLocal(), lineEntity.getField22_equipoLocal());
                if (team != null) {
                    teamsMap.put(team.getId(), team);
                }
                if (teamsMap.size() > 0 && teamsMap.size() % INSERT_BLOCK_SIZE == 0) {
                    teamDAO.insertList(teamsMap.values());
                    teamsMap = new HashMap<>();
                }
                if (linesCount % LINES_BLOCK_SIZE == 0) {
                    logger.debug("updateTeams linesCount ->" + linesCount);
                    release.setLinesTeams(linesCount);
                    releaseDAO.update(release);
                }
            }
        }
        scanner.close();
        teamDAO.insertList(teamsMap.values());
        release.setLinesTeams(linesCount);
        release.setUpdatedTeams(true);
        releaseDAO.update(release);
        logger.debug("last updateTeams linesCount ->" + linesCount);
    }

    @Override
    public void updatePlaces(String idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, DeportesMadridConstants.BUCKET_MATCHES);
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
            if (placesMap.size()>0 && placesMap.size() % INSERT_BLOCK_SIZE == 0) {
                placeDAO.insertList(placesMap.values());
                placesMap = new HashMap<>();
            }
            if (linesCount % LINES_BLOCK_SIZE == 0) {
                logger.debug("updateplaces --> " +linesCount );
                release.setLinesPlaces(linesCount);
                releaseDAO.update(release);
            }
        }
        scanner.close();
        placeDAO.insertList(placesMap.values());
        release.setLinesPlaces(linesCount);
        release.setUpdatedPlaces(true);
        releaseDAO.update(release);
        logger.debug("last updateplaces --> " +linesCount );
    }

    @Override
    public void updateCompetitions(String idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, DeportesMadridConstants.BUCKET_MATCHES);
        scanner.nextLine();
        Map<String, Competition> competitionMap = new HashMap<>();
        Integer linesCount = 1;
        while (scanner.hasNextLine()) {
            linesCount++;
            String line = scanner.nextLine();
            if (release.getLinesCompetitions()<=linesCount) {
                MatchLineEntity matchLineEntity = new MatchLineEntity(line);
                Competition competition = new Competition();
                Integer codTemporada = matchLineEntity.getField00_codTemporada();
                String codCompeticion = matchLineEntity.getField01_codCompeticion();
                Integer codFase = matchLineEntity.getField02_codFase();
                Integer codGrupo = matchLineEntity.getField03_codGrupo();
                String idCompetition = DeportesMadridUtils.generateIdCompetition(codTemporada, codCompeticion, codFase, codGrupo);
                competition.setCodTemporada(codTemporada);
                competition.setCodCompeticion( codCompeticion);
                competition.setCodFase(codFase);
                competition.setCodGrupo(codGrupo);
                competition.setId(idCompetition);
                competition.setNombreTemporada(matchLineEntity.getField15_nombreTemporada());
                competition.setNombreCompeticion(matchLineEntity.getField16_nombreCompeticion());
                competition.setNombreFase(matchLineEntity.getField17_nombreFase());
                competition.setNombreGrupo(matchLineEntity.getField18_nombreGrupo());
                competition.setDeporte(matchLineEntity.getField19_nombreDeporte());
                competition.setDistrito(matchLineEntity.getField26_distrito());
                Competition competitionOriginal = competitionDAO.findById(idCompetition);
                if (competitionOriginal==null || !competitionOriginal.equals(competition)) {
                    competitionMap.put(competition.getId(), competition);
                }
                if (competitionMap.size()>0 && competitionMap.size() % INSERT_BLOCK_SIZE == 0) {
                    competitionDAO.insertList(competitionMap.values());
                    competitionMap = new HashMap<>();
                }
                if (linesCount % LINES_BLOCK_SIZE == 0) {
                    logger.debug("update competitions --> " + linesCount );
                    release.setLinesCompetitions(linesCount);
                    releaseDAO.update(release);
                }
            }
        }
        scanner.close();
        competitionDAO.insertList(competitionMap.values());
        release.setLinesCompetitions(linesCount);
        release.setUpdatedCompetitions(true);
        releaseDAO.update(release);
        logger.debug("last update competitions --> " + linesCount );
    }

    @Override
    public void updateMatches(String idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, DeportesMadridConstants.BUCKET_MATCHES);
        scanner.nextLine();
        Map<String, Match> matchMap = new HashMap<>();
        Integer linesCount = 1;
        while (scanner.hasNextLine()) {
            linesCount++;
            String line = scanner.nextLine();
            if (release.getLinesMatches()<=linesCount) {
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
                String dateStr = matchLineEntity.getField11_fecha() + " " + matchLineEntity.getField12_hora();
                match.setId(idMatch);
                match.setIdCompetition(idCompetition);
                match.setIdTeamLocal(matchLineEntity.getField06_codEquipoLocal());
                match.setIdTeamVisitor(matchLineEntity.getField07_codEquipoVisitante());
                match.setIdPlace(matchLineEntity.getField10_codCampo());
                match.setScoreLocal(matchLineEntity.getField08_scoreLocal());
                match.setScoreVisitor(matchLineEntity.getField09_scoreVisitor());
                match.setDate(DeportesMadridUtils.stringToDate(dateStr));
                match.setNumWeek(matchLineEntity.getField04_weekNum());
                match.setNumMatch(matchLineEntity.getField05_matchNum());
                match.setScheduled(matchLineEntity.getField13_programado()==1);
                match.setState(DeportesMadridConstants.MATCH_STATE.PENDIENTE.getValue());
                if (StringUtils.isNotEmpty(matchLineEntity.getField14_estado())) {
                    match.setState(DeportesMadridConstants.MATCH_STATE.createState(matchLineEntity.getField14_estado().charAt(0)).getValue());
                }
                Match matchOriginal = matchDAO.findById(idMatch);
                if (matchOriginal==null || !matchOriginal.equals(match)) {
                    matchMap.put(match.getId(), match);
                }
                if (matchMap.size()>0 && matchMap.size() % INSERT_BLOCK_SIZE == 0) {
                    logger.debug("1. updateamatches insert size -->" + matchMap.size());
                    matchDAO.insertList(matchMap.values());
                    matchMap = new HashMap<>();
                }
                if (linesCount % LINES_BLOCK_SIZE == 0) {
                    logger.debug("updatematches lines --> " + linesCount );
                    release.setLinesMatches(linesCount);
                    releaseDAO.update(release);
                }
            }
        }
        scanner.close();
        matchDAO.insertList(matchMap.values());
        release.setLinesMatches(linesCount);
        release.setUpdatedMatches(true);
        releaseDAO.update(release);
        logger.debug("2. last update matches --> " + linesCount );

    }

    @Override
    public void updateClassifications(String idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, DeportesMadridConstants.BUCKET_CLASSIFICATION);
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
                Long codEquipo = classificationLineEntity.getField04_codEquipo();
                String idCompetition = DeportesMadridUtils.generateIdCompetition(codTemporada, codCompeticion, codFase, codGrupo);
                String idClassificationEntry = DeportesMadridUtils.generateIdClassification(codTemporada, codCompeticion, codFase, codGrupo, codEquipo);
                ClassificationEntry classificationEntry = new ClassificationEntry();
                classificationEntry.setIdCompetition(idCompetition);
                classificationEntry.setId(idClassificationEntry);
                classificationEntry.setIdTeam(codEquipo);
                classificationEntry.setPosition(classificationLineEntity.getField05_posicion());
                classificationEntry.setPoints(classificationLineEntity.getField06_puntos());
                classificationEntry.setMatchesPlayed(classificationLineEntity.getField07_partidosJugados());
                classificationEntry.setMatchesWon(classificationLineEntity.getField08_partidosGanados());
                classificationEntry.setMatchesDrawn(classificationLineEntity.getField09_partidosEmpatados());
                classificationEntry.setMatchesLost(classificationLineEntity.getField10_partidosPerdidos());
                classificationEntry.setPointsFavor(classificationLineEntity.getField11_golesFavor());
                classificationEntry.setPointsAgainst(classificationLineEntity.getField12_golesContra());
                ClassificationEntry classificationEntryOriginal = classificationDAO.findById(idClassificationEntry);
                if (classificationEntryOriginal==null || !classificationEntry.equals(classificationEntryOriginal)) {
                    classificationEntryMap.put(idClassificationEntry, classificationEntry);
                }
                if (linesCount%LINES_BLOCK_SIZE==0) {
                    logger.debug("4 updateclassifications --> " + linesCount );
                    release.setLinesClassification(linesCount);
                    releaseDAO.update(release);
                }
                if (classificationEntryMap.size()>0 && classificationEntryMap.size()%INSERT_BLOCK_SIZE==0) {
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
        release.setLinesClassification(linesCount);
        release.setUpdatedClassification(true);
        releaseDAO.update(release);
    }


    @Override
    public void updateDataStore() throws Exception {
        Release release = queryLastRelease();
        release.setTaskStart(new Date());
        releaseDAO.update(release);
        if (!release.getUpdatedTeams()) {
            updateTeams(release.getId());
        }
        if (!release.getUpdatedPlaces()) {
            updatePlaces(release.getId());
        }
        if (!release.getUpdatedCompetitions()) {
            updateCompetitions(release.getId());
        }
        if (!release.getUpdatedMatches()) {
            updateMatches(release.getId());
        }
        if (!release.getUpdatedClassification()) {
            updateClassifications(release.getId());
        }
        release.setTaskEnd(new Date());
        releaseDAO.update(release);

    }


    public static final String calculateMd5FromBucket(String bucket, String releaseId) throws IOException {
        GcsFilename gcsFilename = new GcsFilename(bucket, releaseId + ".csv");
        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(gcsFilename, 0, BUFFER_SIZE);
        InputStream inputStream = Channels.newInputStream(readChannel);
        String md5Hex = DigestUtils.md5Hex(inputStream);
        readChannel.close();
        return md5Hex;
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
        //logger.debug(bucketName + " contentLength: " + contentLength);
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
            if (teamOriginal==null || !teamOriginal.equals(teamNew)) {
                return teamNew;
            }
        }
        return null;
    }

    @Override
    public void createRelease() throws Exception {
        Release release = new Release();
        String dateStr = DeportesMadridUtils.dateToString(new Date());
        release.setId(dateStr);
        release.setPublishUrlMatches(DeportesMadridUtils.getLastReleasePublishedUrl(DeportesMadridConstants.URL_MATCHES));
        release.setPublishUrlClassifications(DeportesMadridUtils.getLastReleasePublishedUrl(DeportesMadridConstants.URL_CLASSIFICATION));
        release.setDateStrMatches(DeportesMadridUtils.getLastReleasePublished(DeportesMadridConstants.URL_MATCHES));
        release.setDateStrClassification(DeportesMadridUtils.getLastReleasePublished(DeportesMadridConstants.URL_CLASSIFICATION));
        copyObjectInBucket(DeportesMadridConstants.URL_MATCHES, DeportesMadridConstants.BUCKET_MATCHES, dateStr + ".csv");
        copyObjectInBucket(DeportesMadridConstants.URL_CLASSIFICATION, DeportesMadridConstants.BUCKET_CLASSIFICATION, dateStr + ".csv");
        release.setMd5Matches(calculateMd5FromBucket(DeportesMadridConstants.BUCKET_MATCHES, dateStr));
        release.setMd5Classifications(calculateMd5FromBucket(DeportesMadridConstants.BUCKET_CLASSIFICATION, dateStr));
        release.setUpdatedTeams(false);
        release.setUpdatedPlaces(false);
        release.setUpdatedMatches(false);
        release.setUpdatedCompetitions(false);
        release.setUpdatedClassification(false);
        release.setLinesFileMatches(countLines(dateStr, DeportesMadridConstants.BUCKET_MATCHES));
        release.setLinesFileClassifications(countLines(dateStr, DeportesMadridConstants.BUCKET_CLASSIFICATION));
        release.setLinesTeams(0);
        release.setLinesPlaces(0);
        release.setLinesCompetitions(0);
        release.setLinesMatches(0);
        release.setLinesClassification(0);
        releaseDAO.create(release);
    }

    @Override
    public boolean publishedUpdates(Release release) throws IOException {
        String md5Classification = calculateMd5FromUrl(DeportesMadridConstants.URL_CLASSIFICATION);
        if (!md5Classification.equals(release.getMd5Classifications())) {
            return true;
        } else {
            String md5Matches = calculateMd5FromUrl(DeportesMadridConstants.URL_MATCHES);
            if (!md5Matches.equals(release.getMd5Matches())) {
                return true;
            }
        }
        return false;
    }

    private String calculateMd5FromUrl(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(30000);
        urlConnection.setReadTimeout(30000);
        InputStream inputStream = urlConnection.getInputStream();
        String md5Hex = DigestUtils.md5Hex(inputStream);
        return md5Hex;
    }

    @Override
    public void removeRelease(String id) throws Exception {
        releaseDAO.remove(id);
    }

/*    private Team addOrUpdateTeam(Long idTeam, String nameTeam) throws Exception {
        Team teamNew = null;
        if (idTeam!=null && idTeam!=0 && StringUtils.isNotBlank(nameTeam)) {
            teamNew = new Team();
            teamNew.setId(idTeam);
            teamNew.setName(nameTeam);
        }
        return teamNew;
    }*/
}