package com.adiaz.madrid.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(exclude={"competition", "team"})
public class ClassificationEntry {

    @Id
    private String id;

    @Index
    private String idCompetition;


    @Index
    Long idTeam;

    private Competition competition;
    private Team team;

    @Index
    private Integer position;
    private Integer points;
    private Integer matchesPlayed;
    private Integer matchesWon;
    private Integer matchesDrawn;
    private Integer matchesLost;
    private Integer pointsFavor;
    private Integer pointsAgainst;

}
