package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.*;
import com.adiaz.madrid.entities.*;
import com.adiaz.madrid.utils.entities.ClassificationLineEntity;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.adiaz.madrid.utils.entities.MatchLineEntity;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.taskqueue.TaskOptions;
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
import java.util.logging.Level;

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

    @Autowired
    TeamDAO teamDAO;

    @Autowired
    PlaceDAO placeDAO;

    @Autowired
    GroupDAO groupDAO;

    @Autowired
    MatchDAO matchDAO;

    @Autowired
    ClassificationDAO classificationDAO;

    @Autowired
    ReleaseDAO releaseDAO;

    @Autowired
    TeamManager teamManager;

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
                try {
                    MatchLineEntity lineEntity = new MatchLineEntity(line);
                    Team teamLocal = addOrUpdateTeam(lineEntity.getField06_codEquipoLocal(), lineEntity.getField22_equipoLocal());
                    if (teamLocal != null) {
                        teamsMap.put(teamLocal.getId(), teamLocal);
                    }
                    Team teamVisitor = addOrUpdateTeam(lineEntity.getField07_codEquipoVisitante(), lineEntity.getField23_equipoVisitante());
                    if (teamVisitor != null) {
                        teamsMap.put(teamVisitor.getId(), teamVisitor);
                    }
                } catch (Exception e) {
                    release.setLinesTeamsErrors(release.getLinesTeamsErrors() + 1);
                    releaseDAO.update(release);
                    logger.error("updateTeams ->" + e.getMessage() + " in line:" + line);

                }
                if (linesCount % INSERT_BLOCK_SIZE == 0) {
                    logger.debug("1. updateplaces saving teams " + teamsMap.size());
                    teamDAO.insertList(teamsMap.values());
                    teamsMap = new HashMap<>();
                    release.setLinesTeams(linesCount);
                    releaseDAO.update(release);
                    logger.debug("1. updateTeams linesCount ->" + linesCount);
                }
            }
        }
        scanner.close();
        logger.debug("2. updateplaces saving teams " + teamsMap.size());
        teamDAO.insertList(teamsMap.values());
        release.setLinesTeams(linesCount);
        release.setUpdatedTeams(true);
        releaseDAO.update(release);
        logger.debug("2. updateTeams linesCount ->" + linesCount);
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
            try {
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
            } catch (Exception e) {
                release.setLinesPlacesErrors(release.getLinesPlacesErrors() + 1);
                releaseDAO.update(release);
                logger.error("updatePlaces ->" + e.getMessage() + " in line:" + line);
            }
            if (linesCount % INSERT_BLOCK_SIZE == 0) {
                logger.debug("1. updateplaces saving places " + placesMap.size());
                placeDAO.insertList(placesMap.values());
                placesMap = new HashMap<>();
                release.setLinesPlaces(linesCount);
                releaseDAO.update(release);
                logger.debug("1. updateplaces linesCount ->" + linesCount);
            }
        }
        scanner.close();
        logger.debug("2. updateplaces saving teams " + placesMap.size());
        placeDAO.insertList(placesMap.values());
        release.setLinesPlaces(linesCount);
        release.setUpdatedPlaces(true);
        releaseDAO.update(release);
        logger.debug("2. updateplaces linesCount ->" + linesCount);
    }

    @Override
    public void updateGroups(String idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, DeportesMadridConstants.BUCKET_MATCHES);
        scanner.nextLine();
        Map<String, Group> groupsMap = new HashMap<>();
        Integer linesCount = 1;
        while (scanner.hasNextLine()) {
            linesCount++;
            String line = scanner.nextLine();
            if (release.getLinesGroups()<=linesCount) {
                try {
                    MatchLineEntity matchLineEntity = new MatchLineEntity(line);
                    Group group = new Group();
                    Integer codTemporada = matchLineEntity.getField00_codTemporada();
                    String codCompeticion = matchLineEntity.getField01_codCompeticion();
                    Integer codFase = matchLineEntity.getField02_codFase();
                    Integer codGrupo = matchLineEntity.getField03_codGrupo();
                    String idGroup = DeportesMadridUtils.generateIdGroup(codTemporada, codCompeticion, codFase, codGrupo);
                    group.setCodTemporada(codTemporada);
                    group.setCodCompeticion( codCompeticion);
                    group.setCodFase(codFase);
                    group.setCodGrupo(codGrupo);
                    group.setId(idGroup);
                    group.setNombreTemporada(matchLineEntity.getField15_nombreTemporada());
                    group.setNombreCompeticion(matchLineEntity.getField16_nombreCompeticion());
                    group.setNombreFase(matchLineEntity.getField17_nombreFase());
                    group.setNombreGrupo(matchLineEntity.getField18_nombreGrupo());
                    group.setDeporte(matchLineEntity.getField19_nombreDeporte());
                    group.setCategoria(matchLineEntity.getField20_nombreCategoria());
                    group.setDistrito(matchLineEntity.getField26_distrito());
                    Group groupOriginal = groupDAO.findById(idGroup);
                    if (groupOriginal==null || !groupOriginal.equals(group)) {
                        groupsMap.put(group.getId(), group);
                    }
                } catch (Exception e) {
                    release.setLinesGroupsErrors(release.getLinesGroupsErrors() + 1);
                    releaseDAO.update(release);
                    logger.error("updateGroups ->" + e.getMessage() + " in line:" + line);
                }
                if (linesCount % INSERT_BLOCK_SIZE == 0) {
                    logger.debug("1. updateGroups saving groups " + groupsMap.size());
                    groupDAO.insertList(groupsMap.values());
                    groupsMap = new HashMap<>();
                    release.setLinesGroups(linesCount);
                    releaseDAO.update(release);
                    logger.debug("1. updateGroups --> " + linesCount );
                }
            }
        }
        scanner.close();
        logger.debug("2. updateGroups saving groups " + groupsMap.size());
        groupDAO.insertList(groupsMap.values());
        release.setLinesGroups(linesCount);
        release.setUpdatedGroups(true);
        releaseDAO.update(release);
        logger.debug("2. updateGroups --> " + linesCount );
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
                try {
                    MatchLineEntity matchLineEntity = new MatchLineEntity(line);
                    Match match = new Match();
                    //find group
                    Integer codTemporada = matchLineEntity.getField00_codTemporada();
                    String codCompeticion = matchLineEntity.getField01_codCompeticion();
                    Integer codFase = matchLineEntity.getField02_codFase();
                    Integer codGrupo = matchLineEntity.getField03_codGrupo();
                    Integer weekNumber = matchLineEntity.getField04_weekNum();
                    Integer matchNumber = matchLineEntity.getField05_matchNum();
                    String idGroup = DeportesMadridUtils.generateIdGroup(codTemporada, codCompeticion, codFase, codGrupo);
                    String idMatch = DeportesMadridUtils.generateIdMatch(codTemporada, codCompeticion, codFase, codGrupo, weekNumber, matchNumber);
                    String dateStr = matchLineEntity.getField11_fecha() + " " + matchLineEntity.getField12_hora();
                    match.setId(idMatch);
                    match.setIdGroup(idGroup);
                    match.setIdTeamLocal(matchLineEntity.getField06_codEquipoLocal());
                    match.setIdTeamVisitor(matchLineEntity.getField07_codEquipoVisitante());
                    match.setIdPlace(matchLineEntity.getField10_codCampo());
                    match.setScoreLocal(matchLineEntity.getField08_scoreLocal());
                    match.setScoreVisitor(matchLineEntity.getField09_scoreVisitor());
                    match.setDate(DeportesMadridUtils.stringToDate(dateStr));
                    match.setNumWeek(matchLineEntity.getField04_weekNum());
                    match.setNumMatch(matchLineEntity.getField05_matchNum());
                    match.setScheduled(matchLineEntity.getField13_programado()==1);
                    match.setState(calculateState(matchLineEntity));
                    Match matchOriginal = matchDAO.findById(idMatch);
                    if (matchOriginal==null || !matchOriginal.equals(match)) {
                        matchMap.put(match.getId(), match);
                    }
                } catch (Exception e) {
                    release.setLinesMatchesErrors(release.getLinesMatchesErrors() + 1);
                    releaseDAO.update(release);
                    logger.error("updateMatches ->" + e.getMessage() + " in line:" + line);
                }
                if (linesCount % INSERT_BLOCK_SIZE == 0) {
                    logger.debug("1. updateMatches saving places " + matchMap.size());
                    matchDAO.insertList(matchMap.values());
                    matchMap = new HashMap<>();
                    release.setLinesMatches(linesCount);
                    releaseDAO.update(release);
                    logger.debug("updatematches lines --> " + linesCount );
                }
            }
        }
        scanner.close();
        logger.debug("2. updateMatches saving places " + matchMap.size());
        matchDAO.insertList(matchMap.values());
        release.setLinesMatches(linesCount);
        release.setUpdatedMatches(true);
        releaseDAO.update(release);
        logger.debug("2. last update matches --> " + linesCount );

    }

    private Integer calculateState(MatchLineEntity matchLineEntity) {
        Integer state = DeportesMadridConstants.MATCH_STATE.PENDIENTE.getValue();
        if ((matchLineEntity.getField06_codEquipoLocal()==0 && matchLineEntity.getField07_codEquipoVisitante()!=0)
                || (matchLineEntity.getField06_codEquipoLocal()!=0 && matchLineEntity.getField07_codEquipoVisitante()==0)) {
            state = DeportesMadridConstants.MATCH_STATE.DESCANSA.getValue();
        } else {
            if (StringUtils.isNotEmpty(matchLineEntity.getField14_estado())) {
                state = DeportesMadridConstants.MATCH_STATE.createState(matchLineEntity.getField14_estado().charAt(0)).getValue();
            }
        }
        return state;
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
            if (release.getLinesClassification()<=linesCount) {
                try {
                    ClassificationLineEntity classificationLineEntity = new ClassificationLineEntity(line);
                    Integer codTemporada = classificationLineEntity.getField00_codTemporada();
                    String codCompeticion = classificationLineEntity.getField01_codCompeticion();
                    Integer codFase = classificationLineEntity.getField02_codFase();
                    Integer codGrupo = classificationLineEntity.getField03_codGrupo();
                    Long codEquipo = classificationLineEntity.getField04_codEquipo();
                    String idGroup = DeportesMadridUtils.generateIdGroup(codTemporada, codCompeticion, codFase, codGrupo);
                    String idClassificationEntry = DeportesMadridUtils.generateIdClassification(codTemporada, codCompeticion, codFase, codGrupo, codEquipo);
                    ClassificationEntry classificationEntry = new ClassificationEntry();
                    classificationEntry.setIdGroup(idGroup);
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
                } catch (Exception e) {
                    release.setLinesClassificationErrors(release.getLinesClassificationErrors() + 1);
                    releaseDAO.update(release);
                    logger.error("updateClassifications ->" + e.getMessage() + " in line:" + line);
                }
                if (linesCount%INSERT_BLOCK_SIZE == 0) {
                    logger.debug("1. updateclassifications saving entities " + classificationEntryMap.size());
                    classificationDAO.insertList(classificationEntryMap.values());
                    classificationEntryMap = new HashMap<>();
                    release.setLinesClassification(linesCount);
                    releaseDAO.update(release);
                    logger.debug("1. updateclassifications --> " + linesCount );
                }
            }
        }
        scanner.close();
        logger.debug("2. updateclassifications saving entities " + classificationEntryMap.size());
        classificationDAO.insertList(classificationEntryMap.values());
        release.setLinesClassification(linesCount);
        release.setUpdatedClassification(true);
        releaseDAO.update(release);
        logger.debug("2. updateclassifications --> " + linesCount );
    }

    @Override
    public void updateTeamsGroups(String idRelease) throws Exception {
        Release release = releaseDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, DeportesMadridConstants.BUCKET_MATCHES);
        scanner.nextLine();
        int linesCount = 1;
        Map<Long, Team> map = new HashMap<>();
        while (scanner.hasNextLine()) {
            linesCount++;
            String line = scanner.nextLine();
            if (release.getLinesTeamsGroups() <= linesCount) {
                try {
                    MatchLineEntity lineEntity = new MatchLineEntity(line);
                    Integer codTemporada = lineEntity.getField00_codTemporada();
                    String codCompeticion = lineEntity.getField01_codCompeticion();
                    Integer codFase = lineEntity.getField02_codFase();
                    Integer codGrupo = lineEntity.getField03_codGrupo();
                    Long teamId = lineEntity.getField06_codEquipoLocal();
                    if (teamId!=null && teamId!=0) {
                        String idGroup = DeportesMadridUtils.generateIdGroup(codTemporada, codCompeticion, codFase, codGrupo);
                        Team teamLocal = teamDAO.findById(teamId);
                        if (teamLocal!=null && !map.containsKey(teamLocal.getId()) && !teamLocal.getGroups().contains(idGroup)) {
                            teamLocal.getGroups().add(idGroup);
                            map.put(teamLocal.getId(), teamLocal);
                        }
                    }
                } catch (Exception e) {
                    release.setLinesTeamsGroupsErrors(release.getLinesTeamsGroupsErrors() + 1);
                    releaseDAO.update(release);
                    logger.error("updateTeamsGroups ->" + e.getMessage() + " in line:" + line);
                }
                if (linesCount%INSERT_BLOCK_SIZE == 0) {
                    logger.debug("1. updateTeamsGroups saving entities " + map.size());
                    teamDAO.insertList(map.values());
                    map = new HashMap<>();
                    release.setLinesTeamsGroups(linesCount);
                    releaseDAO.update(release);
                    logger.debug("1. updateTeamsGroups --> " + linesCount );
                }
            }
        }
        scanner.close();
        logger.debug("2. updateTeamsGroups saving entities " + map.size());
        teamDAO.insertList(map.values());
        release.setLinesTeamsGroups(linesCount);
        release.setUpdatedTeamsGroups(true);
        releaseDAO.update(release);
        logger.debug("2. updateTeamsGroups --> " + linesCount );

    }

    @Override
    public void enqueTaskAll() throws Exception {
        Release release = queryLastRelease();
        release.setTaskEnqued(new Date());
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    private void enqueDefaultTask() {
        Queue queue = QueueFactory.getDefaultQueue();
        RetryOptions retryOptions = RetryOptions.Builder.withTaskRetryLimit(10);
        queue.add(TaskOptions.Builder.withUrl(DeportesMadridConstants.PATH_ENQUE_TASK).retryOptions(retryOptions));
    }

    @Override
    public void enqueTaskPlaces() throws Exception {
        Release release = queryLastRelease();
        release.setUpdatedPlaces(false);
        release.setLinesPlaces(0);
        release.setLinesPlacesErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    @Override
    public void enqueTaskTeams() throws Exception {
        Release release = queryLastRelease();
        release.setUpdatedTeams(false);
        release.setLinesTeams(0);
        release.setLinesTeamsErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    @Override
    public void enqueTaskGroups() throws Exception {
        Release release = queryLastRelease();
        release.setUpdatedGroups(false);
        release.setLinesGroups(0);
        release.setLinesGroupsErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    @Override
    public void enqueTaskMatches() throws Exception {
        Release release = queryLastRelease();
        release.setUpdatedMatches(false);
        release.setLinesMatches(0);
        release.setLinesMatchesErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    @Override
    public void enqueTaskClassification() throws Exception {
        Release release = queryLastRelease();
        release.setUpdatedClassification(false);
        release.setLinesClassification(0);
        release.setLinesClassificationErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    @Override
    public void enqueTaskEntities() throws Exception {
        Release release = queryLastRelease();
        release.setUpdatedTeamsGroups(false);
        release.setLinesTeamsGroups(0);
        release.setLinesTeamsGroupsErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
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
        if (!release.getUpdatedGroups()) {
            updateGroups(release.getId());
        }
        if (!release.getUpdatedMatches()) {
            updateMatches(release.getId());
        }
        if (!release.getUpdatedClassification()) {
            updateClassifications(release.getId());
        }
        if (!release.getUpdatedTeamsGroups()) {
            updateTeamsGroups(release.getId());
        }
        release.setTaskEnd(new Date());
        releaseDAO.update(release);
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        syncCache.clearAll();
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

    private Team addOrUpdateTeam(Long teamId, String teamName) throws Exception {
        if (teamId!=null && teamId!=0 && StringUtils.isNotBlank(teamName)) {
            Team teamNew = new Team();
            teamNew.setId(teamId);
            teamNew.setName(teamName);
            Team teamOriginal = teamDAO.findById(teamId);
            if (teamOriginal==null || !teamOriginal.getName().equals(teamNew.getName())) {
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
        release.setUpdatedGroups(false);
        release.setUpdatedClassification(false);
        release.setUpdatedTeamsGroups(false);
        release.setLinesFileMatches(countLines(dateStr, DeportesMadridConstants.BUCKET_MATCHES));
        release.setLinesFileClassifications(countLines(dateStr, DeportesMadridConstants.BUCKET_CLASSIFICATION));
        release.setLinesTeams(0);
        release.setLinesPlaces(0);
        release.setLinesGroups(0);
        release.setLinesMatches(0);
        release.setLinesClassification(0);
        release.setLinesTeamsGroups(0);
        release.setLinesTeamsErrors(0);
        release.setLinesPlacesErrors(0);
        release.setLinesGroupsErrors(0);
        release.setLinesMatchesErrors(0);
        release.setLinesClassificationErrors(0);
        release.setLinesTeamsGroupsErrors(0);
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