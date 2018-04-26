package com.adiaz.madrid.utils;

import com.adiaz.madrid.daos.ReleaseDAO;
import com.adiaz.madrid.entities.Match;
import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.utils.entities.MatchLineEntity;
import com.google.appengine.tools.cloudstorage.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class UpdateEntity <E> {

    @Autowired
    ReleaseDAO releaseDAO;


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

    void updateEntity (String idRelease) {
        Release release = releaseDAO.findById(idRelease);
        Scanner scanner = getScanner(idRelease, DeportesMadridConstants.BUCKET_MATCHES);
        scanner.nextLine();
        Map<String, Match> matchMap = new HashMap<>();
        Integer linesCount = 1;
        while (scanner.hasNextLine()) {

        }
        scanner.close();
    }


    abstract E generateNewEntity(MatchLineEntity matchLineEntity);


    private Scanner getScanner(String releaseId, String bucket) {
        GcsFilename gcsFilename = new GcsFilename(bucket, releaseId + ".csv");
        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(gcsFilename, 0, BUFFER_SIZE);
        InputStream inputStream = Channels.newInputStream(readChannel);
        return new Scanner(inputStream, StandardCharsets.ISO_8859_1.name());
    }

}
