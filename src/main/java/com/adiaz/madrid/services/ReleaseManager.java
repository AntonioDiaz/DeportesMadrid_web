package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.Release;

import java.util.List;

public interface ReleaseManager {

    void addRelease(Release release) throws Exception;

    boolean updateRelease(Release release) throws Exception;

    void removeRelease(Long id) throws Exception;

    List<Release> queryAllRelease();

    Release lastRelease();

    void createEmptyRelease() throws Exception;

    void loadBucket(Long idRelease) throws Exception;

    void updateTeams(Long idRelease) throws Exception;

    void updatePlaces(Long idRelease) throws Exception;

    void updateCompetitions(Long idRelease) throws Exception;

    void updateMatches(Long idRelease) throws Exception;

    void updateClassifications(Long idRelease) throws Exception;
}
