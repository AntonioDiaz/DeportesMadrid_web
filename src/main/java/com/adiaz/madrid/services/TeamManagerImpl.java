package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.TeamDAO;
import com.adiaz.madrid.entities.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service ("teamManager")
public class TeamManagerImpl implements TeamManager {

    @Autowired
    TeamDAO teamDAO;

    @Override
    public int teamsCount() {
        return teamDAO.findAll().size();
    }

    @Override
    public List<Team> findTeams(String teamName) {
        List<Team> teams = new ArrayList<>();
        for (Team team : teamDAO.findAll()) {
            if (team.getName().contains(teamName)) {
                teams.add(team);
            }
        }
        return teams;
    }
}
