package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.Release;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ReleaseManager {
    
    List<Release> queryAllRelease();
    
    Release queryLastRelease();

    void updateTeams(String idRelease) throws Exception;

    void updatePlaces(String idRelease) throws Exception;

    void updateGroups(String idRelease) throws Exception;

    Set<String> updateMatches(String idRelease) throws Exception;

    void updateClassifications(String idRelease) throws Exception;
    void updateTeamsGroups(String idRelease) throws Exception;


    void createRelease() throws Exception;

    boolean publishedUpdates(Release release) throws IOException;

    void removeRelease(String id) throws Exception;

    void updateDataStore() throws Exception;

    void enqueTaskAll() throws Exception;

    void enqueTaskPlaces() throws Exception;
    void enqueTaskTeams() throws Exception;
    void enqueTaskGroups() throws Exception;
    void enqueTaskMatches() throws Exception;
    void enqueTaskClassification() throws Exception;
    void enqueTaskEntities() throws Exception;
}
