package com.adiaz.madrid.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode
public class ClassificationEntry {

    @Id
    private String id;

    @Load
    @Index
    @JsonIgnore
    private Ref<Competition> competitionRef;
    private Competition competition;

    @Load
    @Index
    @JsonIgnore
    private Ref<Team> teamRef;
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

    @OnLoad
    public void getRefs(){
        if (competitionRef!=null && competitionRef.isLoaded()) {
            competition = competitionRef.get();
        }
        if (teamRef!=null && teamRef.isLoaded()) {
            team = teamRef.get();
        }
    }
}
