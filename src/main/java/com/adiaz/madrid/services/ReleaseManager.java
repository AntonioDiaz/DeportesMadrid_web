package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.Release;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ReleaseManager {
    
    List<Release> queryAllRelease();
    
    Release queryLastRelease();
    Release queryReleaseById(String id);

    void createRelease() throws Exception;

    boolean publishedUpdates(Release release) throws IOException;

    void removeRelease(String id) throws Exception;

    void updateDataStore(Release release) throws Exception;
    void updateDataStore() throws Exception;

    void enqueTaskAll(Release release) throws Exception;

    void enqueTaskPlaces(Release release) throws Exception;
    void enqueTaskTeams(Release release) throws Exception;
    void enqueTaskGroups(Release release) throws Exception;
    void enqueTaskMatches(Release release) throws Exception;
    void enqueTaskClassification(Release release) throws Exception;
    void enqueTaskEntities(Release release) throws Exception;
}
