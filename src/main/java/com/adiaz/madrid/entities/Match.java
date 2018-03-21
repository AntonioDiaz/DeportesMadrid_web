package com.adiaz.madrid.entities;


import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Entity
@Data
@EqualsAndHashCode
public class Match {

    @Id
    private String id;

    @Load
    @Index
    @JsonIgnore
    private Ref<Competition> competitionRef;
    private Competition competition;

    @Index
    private Integer weekNum;
    @Index
    private Integer matchNum;

    @Load
    @Index
    @JsonIgnore
    private Ref<Team> teamLocalRef;
    private Team teamLocal;

    @Load
    @Index
    @JsonIgnore
    private Ref<Team> teamVisitorRef;
    private Team teamVisitor;

    @Load
    @Index
    @JsonIgnore
    private Ref<Place> placeRef;
    private Place place;

    private Integer scoreLocal;
    private Integer scoreVisitor;
    private Integer state;

    private Boolean scheduled;

    @Index
    private Date date;

    @OnLoad
    public void getRefs(){
        if (competitionRef!=null && competitionRef.isLoaded()) {
            competition = competitionRef.get();
        }
        if (teamLocalRef!=null && teamLocalRef.isLoaded()) {
            teamLocal = teamLocalRef.get();
        }
        if (teamVisitorRef!=null && teamVisitorRef.isLoaded()) {
            teamVisitor = teamVisitorRef.get();
        }
        if (placeRef!=null && placeRef.isLoaded()) {
            place = placeRef.get();
        }
    }

}
