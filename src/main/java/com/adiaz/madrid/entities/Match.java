package com.adiaz.madrid.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity (name = "MATCH_ENTITY")
@Data
@EqualsAndHashCode(exclude={"group", "teamLocal", "teamVisitor", "place"})
public class Match {

    @Id
    private String id;

    @Index
    private String idGroup;

    @Index
    private Long idTeamLocal;

    @Index
    private Long idTeamVisitor;

    @Index
    private Long idPlace;

    @Ignore
    @JsonIgnore
    private Group group;

    @Ignore
    private Team teamLocal;

    @Ignore
    private Team teamVisitor;

    @Ignore
    private Place place;

    @Index
    private Integer numWeek;

    @Index
    private Integer numMatch;

    private Integer scoreLocal;
    private Integer scoreVisitor;
    private Integer state;
    private Boolean scheduled;

    @Index
    private Date date;
}
