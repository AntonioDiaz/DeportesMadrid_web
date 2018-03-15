package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.Team;

import java.util.List;

public interface TeamManager {

    int teamsCount();

    List<Team> findTeams(String teamName);

}
