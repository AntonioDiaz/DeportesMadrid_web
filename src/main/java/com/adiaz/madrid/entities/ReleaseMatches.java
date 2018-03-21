package com.adiaz.madrid.entities;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity
@Data
@EqualsAndHashCode
public class ReleaseMatches {
    @Id
    String id;
    String publishUrl;
    Date dateInitLoad;
    Date dateEndLoad;
    Boolean updatedBucket;
    Boolean updatedTeams;
    Boolean updatedPlaces;
    Boolean updatedCompetitions;
    Boolean updatedMatches;
    Integer lines;
    Integer linesTeams;
    Integer linesPlaces;
    Integer linesCompetitions;
    Integer linesMatches;
}
