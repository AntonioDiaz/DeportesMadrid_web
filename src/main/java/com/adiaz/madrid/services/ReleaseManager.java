package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.ReleaseClassification;
import com.adiaz.madrid.entities.ReleaseMatches;

import java.util.List;

public interface ReleaseManager {

    void removeReleaseMatches(String id) throws Exception;

    void removeReleaseClassification(String id) throws Exception;

    List<ReleaseMatches> queryAllReleaseMatches();

    List<ReleaseClassification> queryAllReleaseClassifications();

    ReleaseMatches queryReleaseMatches(String id);

    ReleaseClassification queryReleaseClassifications(String id);

    ReleaseMatches createOrGetLastReleasePublishedMatches() throws Exception;

    ReleaseClassification createOrGetLastReleasePublishedClassification() throws Exception;

    void loadBucketMatches(String idRelease) throws Exception;

    void loadBucketClassification(String idRelease) throws Exception;

    void updateTeams(String idRelease) throws Exception;

    void updatePlaces(String idRelease) throws Exception;

    void updateCompetitions(String idRelease) throws Exception;

    void updateMatches(String idRelease) throws Exception;

    void updateClassifications(String idRelease) throws Exception;

    void updateDataStore() throws Exception;
}
