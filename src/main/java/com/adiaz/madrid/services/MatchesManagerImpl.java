package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.GroupDAO;
import com.adiaz.madrid.daos.MatchDAO;
import com.adiaz.madrid.daos.PlaceDAO;
import com.adiaz.madrid.daos.TeamDAO;
import com.adiaz.madrid.entities.Group;
import com.adiaz.madrid.entities.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MatchesManagerImpl implements MatchesManager {

    @Autowired
    MatchDAO matchDAO;

    @Autowired
    GroupDAO groupDAO;

    @Autowired
    TeamDAO teamDAO;

    @Autowired
    PlaceDAO placeDAO;

    @Override
    public int matchesCount() {
        return matchDAO.recordsCount();
    }

    @Override
    public List<Match> findMatchesByIdGroup(String idGroup) {
        Group group = groupDAO.findById(idGroup);
        List<Match> matchList = matchDAO.findByCompeticion(idGroup);
        for (Match match : matchList) {
            match.setGroup(group);
            match.setTeamLocal(teamDAO.findById(match.getIdTeamLocal()));
            match.setTeamVisitor(teamDAO.findById(match.getIdTeamVisitor()));
            match.setPlace(placeDAO.findById(match.getIdPlace()));
        }
        return matchList;
    }
}
