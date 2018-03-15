package com.adiaz.madrid.entities;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Release {
    @Id
    private Long id;
    String publishUrlMatches;
    String publishUrlClassification;
    String publishDateStr;
    Date dateInitLoad;
    Date dateEndLoad;
    Integer recordsClassification;
    Integer recordsMatches;
    Boolean updatedBucket;
    Boolean updatedTeams;
    Boolean updatedPlaces;
    Boolean updateCompetitions;
    Boolean updatedMatches;
    Boolean updatedClassification;
}
