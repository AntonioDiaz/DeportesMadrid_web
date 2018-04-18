package com.adiaz.madrid.utils.entities;

import lombok.Data;

@Data
public class SportsCountEntity {
    String sportName;
    Integer competitionsCount;

    public SportsCountEntity(String sportName, Integer competitionsCount) {
        this.sportName = sportName;
        this.competitionsCount = competitionsCount;
    }
}
