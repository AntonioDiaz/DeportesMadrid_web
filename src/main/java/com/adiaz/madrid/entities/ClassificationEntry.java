package com.adiaz.madrid.entities;

import com.googlecode.objectify.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity (name = "CLASSIFICATION_ENTITY")
@Data
@EqualsAndHashCode(exclude={"group", "team"})
public class ClassificationEntry {

    @Id
    private String id;

    @Index
    private String idGroup;


    @Index
    Long idTeam;

    @Ignore
    private Group group;

    @Ignore
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
