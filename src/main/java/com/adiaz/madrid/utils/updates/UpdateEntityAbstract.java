package com.adiaz.madrid.utils.updates;

import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.services.ReleaseManagerImpl;
import com.adiaz.madrid.utils.entities.MatchLineEntity;
import com.google.appengine.tools.cloudstorage.*;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class UpdateEntityAbstract <T> {

    private static final Logger logger = Logger.getLogger(UpdateEntityAbstract.class);


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

    abstract int getLinesProcessed(Release release);
    abstract void addOneLineError(Release release) throws Exception;
    abstract Set<String[]> addEntityToMap(Map map, String line) throws Exception;
    abstract void insertEntities(Release release, Collection<T> entitiesToUpdate, int linesUpdated, boolean ended) throws Exception;

    public Set<String[]> update(Release release, String urlBucket) throws Exception {
        Set<String[]> teamsUpdated = new HashSet<>();
        Scanner scanner = getScanner(release.getId(), urlBucket);
        scanner.nextLine();
        int linesCount = 1;
        Map<Long, T> map = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            linesCount++;
            if (getLinesProcessed(release)<=linesCount) {
                try {
                    teamsUpdated.addAll(addEntityToMap(map, line));
                } catch (Exception e) {
                    logger.error("error update: line" + line, e);
                    addOneLineError(release);
                }
                if (linesCount % INSERT_BLOCK_SIZE==0) {
                    insertEntities(release, map.values(), linesCount, false);
                    map = new HashMap<>();
                }
            }
        }
        insertEntities(release, map.values(), linesCount, true);
        scanner.close();
        return teamsUpdated;
    }

    private Scanner getScanner(String releaseId, String bucket) {
        GcsFilename gcsFilename = new GcsFilename(bucket, releaseId + ".csv");
        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(gcsFilename, 0, BUFFER_SIZE);
        InputStream inputStream = Channels.newInputStream(readChannel);
        return new Scanner(inputStream, StandardCharsets.ISO_8859_1.name());
    }
}
