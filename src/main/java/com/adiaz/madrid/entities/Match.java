package com.adiaz.madrid.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(exclude={"competition", "teamLocal", "teamVisitor", "place"})
public class Match {

    @Id
    private String id;

    @Index
    private String idCompetition;

    @Index
    private Long idTeamLocal;


    @Index
    private Long idTeamVisitor;


    @Index
    private Long idPlace;

    private Competition competition;
    private Team teamLocal;
    private Team teamVisitor;
    private Place place;

    @Index
    private Integer weekNum;

    @Index
    private Integer matchNum;

    private Integer scoreLocal;
    private Integer scoreVisitor;
    private Integer state;
    private Boolean scheduled;

    @Index
    private Date date;
}
