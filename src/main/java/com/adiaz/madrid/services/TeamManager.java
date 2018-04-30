package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.Team;

import java.util.List;
import java.util.Set;

public interface TeamManager {

    int teamsCount();

    List<Team> findTeams(String teamName);

    List<Team> findAll();

/*
    Set<String> teamGroups(Long idTeam);
    void updateTeamsGroups();
*/

}
