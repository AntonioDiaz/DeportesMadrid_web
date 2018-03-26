package com.adiaz.madrid.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity
@Data
@EqualsAndHashCode
public class Release {

    @Id
    String id;

    String dateStrMatches;
    String dateStrClassification;
    Date taskStart;
    Date taskEnd;

    String publishUrlMatches;
    String publishUrlClassifications;

    String md5Matches;
    String md5Classifications;


    Integer linesFileClassifications;
    Integer linesFileMatches;

    Boolean updatedTeams;
    Boolean updatedPlaces;
    Boolean updatedCompetitions;
    Boolean updatedMatches;
    Boolean updatedClassification;

    Integer linesTeams;
    Integer linesPlaces;
    Integer linesCompetitions;
    Integer linesMatches;
    Integer linesClassification;

}
