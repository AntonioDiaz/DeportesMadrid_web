package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.Release;

import java.io.IOException;
import java.util.List;

public interface ReleaseManager {
    
    List<Release> queryAllRelease();
    
    Release queryLastRelease();

    void updateTeams(String idRelease) throws Exception;

    void updatePlaces(String idRelease) throws Exception;

    void updateGroups(String idRelease) throws Exception;

    void updateMatches(String idRelease) throws Exception;

    void updateClassifications(String idRelease) throws Exception;

    void createRelease() throws Exception;

    boolean publishedUpdates(Release release) throws IOException;

    void removeRelease(String id) throws Exception;

    void updateDataStore() throws Exception;
}
