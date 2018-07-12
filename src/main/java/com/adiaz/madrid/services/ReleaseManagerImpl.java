package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.*;
import com.adiaz.madrid.entities.Parameter;
import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.adiaz.madrid.utils.updates.*;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.cloudstorage.*;
import org.apache.commons.codec.digest.DigestUtils;
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
    ReleaseDAO releaseDAO;

    @Autowired
    TeamManager teamManager;

    @Autowired
    ParametersManager parametersManager;

    @Autowired
    UpdateEntityPlaces updatePlaces;

    @Autowired
    UpdateEntityTeams updateTeams;

    @Autowired
    UpdateEntityGroups updateGroups;

    @Autowired
    UpdateEntityMatches updateMatches;

    @Autowired
    UpdateEntityClassification updateClassification;

    @Autowired
    UpdateEntityTeamsGroups updateEntityTeamsGroups;

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
    public Release queryReleaseById(String id) {
        return releaseDAO.findById(id);
    }


    @Override
    public void enqueTaskAll(Release release) throws Exception {
        release.setTaskEnqued(new Date());
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    @Override
    public void enqueTaskPlaces(Release release) throws Exception {
        release.setUpdatedPlaces(false);
        release.setLinesPlaces(0);
        release.setLinesPlacesErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    @Override
    public void enqueTaskTeams(Release release) throws Exception {
        release.setUpdatedTeams(false);
        release.setLinesTeams(0);
        release.setLinesTeamsErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    @Override
    public void enqueTaskGroups(Release release) throws Exception {
        release.setUpdatedGroups(false);
        release.setLinesGroups(0);
        release.setLinesGroupsErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    @Override
    public void enqueTaskMatches(Release release) throws Exception {
        release.setUpdatedMatches(false);
        release.setLinesMatches(0);
        release.setLinesMatchesErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    @Override
    public void enqueTaskClassification(Release release) throws Exception {
        release.setUpdatedClassification(false);
        release.setLinesClassification(0);
        release.setLinesClassificationErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
    }

    @Override
    public void enqueTaskEntities(Release release) throws Exception {
        release.setUpdatedTeamsGroups(false);
        release.setLinesTeamsGroups(0);
        release.setLinesTeamsGroupsErrors(0);
        releaseDAO.update(release);
        enqueDefaultTask();
    }


    @Override
    public void updateDataStore() throws Exception {
        updateDataStore(queryLastRelease());
    }

    @Override
    public void updateDataStore(Release release) throws Exception {
        release.setTaskStart(new Date());
        releaseDAO.update(release);
        Set<String[]> teamsUpdated = new HashSet<>();
        if (!release.getUpdatedTeams()) {
            updateTeams.update(release, DeportesMadridConstants.BUCKET_MATCHES);
        }
        if (!release.getUpdatedPlaces()) {
            updatePlaces.update(release, DeportesMadridConstants.BUCKET_MATCHES);
        }
        if (!release.getUpdatedGroups()) {
            updateGroups.update(release, DeportesMadridConstants.BUCKET_MATCHES);
        }
        if (!release.getUpdatedMatches()) {
            teamsUpdated.addAll(updateMatches.update(release, DeportesMadridConstants.BUCKET_MATCHES));
        }
        if (!release.getUpdatedClassification()) {
            teamsUpdated.addAll(updateClassification.update(release, DeportesMadridConstants.BUCKET_CLASSIFICATION));
        }
        if (!release.getUpdatedTeamsGroups()) {
            updateEntityTeamsGroups.update(release, DeportesMadridConstants.BUCKET_MATCHES);
        }
        String fcmKeyServer = parametersManager.queryByKey(DeportesMadridConstants.PARAMETER_FCM_SERVER_KEY).getValue();
        DeportesMadridUtils.sendNotificationToFirebase(fcmKeyServer, teamsUpdated);
        release.setTaskEnd(new Date());
        releaseDAO.update(release);
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        syncCache.clearAll();
    }

    private static String calculateMd5FromBucket(String bucket, String releaseId) throws IOException {
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
        InputStream inputStream = urlConnection.getInputStream();
        copy(inputStream, Channels.newOutputStream(outputChannel));
    }

    @Override
    public void createRelease() throws Exception {
        Release release = new Release();
        String dateStr = DeportesMadridUtils.dateToString(new Date());
        release.setId(dateStr);
        release.setPublishUrlMatches(DeportesMadridUtils.getLastReleasePublishedUrl(getUrlMatches()));
        release.setPublishUrlClassifications(DeportesMadridUtils.getLastReleasePublishedUrl(getUrlClassification()));
        release.setDateStrMatches(DeportesMadridUtils.getLastReleasePublished(getUrlMatches()));
        release.setDateStrClassification(DeportesMadridUtils.getLastReleasePublished(getUrlClassification()));
        copyObjectInBucket(getUrlMatches(), DeportesMadridConstants.BUCKET_MATCHES, dateStr + ".csv");
        copyObjectInBucket(getUrlClassification(), DeportesMadridConstants.BUCKET_CLASSIFICATION, dateStr + ".csv");
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
        /*check if there are any change in server file. */
        if (release==null) {
            return true;
        } else {
            String currentmd5Classification = calculateMd5FromUrl(getUrlClassification());
            if (!currentmd5Classification.equals(release.getMd5Classifications())) {
                return true;
            } else {
                String md5Matches = calculateMd5FromUrl(getUrlMatches());
                if (!md5Matches.equals(release.getMd5Matches())) {
                    return true;
                }
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
        return DigestUtils.md5Hex(inputStream);
    }

    @Override
    public void removeRelease(String id) throws Exception {
        releaseDAO.remove(id);
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


    private String getUrlMatches(){
        String urlMatches = "";
        Parameter paramMatches = parametersManager.queryByKey(DeportesMadridConstants.PARAMETER_URL_MATCHES);
        if (paramMatches !=null) {
            urlMatches = paramMatches.getValue();
        }
        Parameter parameter = parametersManager.queryByKey(DeportesMadridConstants.PARAMETER_DEBUG);
        if (parameter!=null) {
            if (Boolean.valueOf(parameter.getValue())) {
                urlMatches = DeportesMadridConstants.URL_MATCHES_FAKE;
            }
        }
        return urlMatches;
    }

    private String getUrlClassification(){
        String urlClassification = "";
        Parameter paramUrlClassification = parametersManager.queryByKey(DeportesMadridConstants.PARAMETER_URL_CLASSIFICATION);
        if (paramUrlClassification !=null) {
            urlClassification = paramUrlClassification.getValue();
        }
        Parameter parameter = parametersManager.queryByKey(DeportesMadridConstants.PARAMETER_DEBUG);
        if (parameter!=null) {
            if (Boolean.valueOf(parameter.getValue())) {
                urlClassification = DeportesMadridConstants.URL_CLASSIFICATION_FAKE;
            }
        }
        return urlClassification;
    }

    private void enqueDefaultTask() {
        Queue queue = QueueFactory.getDefaultQueue();
        RetryOptions retryOptions = RetryOptions.Builder.withTaskRetryLimit(10);
        queue.add(TaskOptions.Builder.withUrl(DeportesMadridConstants.PATH_ENQUE_TASK).retryOptions(retryOptions));
    }

}