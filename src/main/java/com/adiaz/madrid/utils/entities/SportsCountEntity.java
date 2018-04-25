package com.adiaz.madrid.utils.entities;

import lombok.Data;

@Data
public class SportsCountEntity {
    String sportName;
    Integer groupsCount;

    public SportsCountEntity(String sportName, Integer groupsCount) {
        this.sportName = sportName;
        this.groupsCount = groupsCount;
    }
}
