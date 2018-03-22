package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.CompetitionDAO;
import com.adiaz.madrid.daos.MatchDAO;
import com.adiaz.madrid.daos.PlaceDAO;
import com.adiaz.madrid.daos.TeamDAO;
import com.adiaz.madrid.entities.Competition;
import com.adiaz.madrid.entities.Match;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MatchesManagerImpl implements MatchesManager {

    @Autowired
    MatchDAO matchDAO;

    @Autowired
    CompetitionDAO competitionDAO;

    @Autowired
    TeamDAO teamDAO;

    @Autowired
    PlaceDAO placeDAO;

    @Override
    public int matchesCount() {
        return matchDAO.recordsCount();
    }

    @Override
    public List<Match> findMatchesByCompetition(String idCompeticion) {
        Competition competition = competitionDAO.findCompetition(idCompeticion);
        List<Match> matchList = matchDAO.findByCompeticion(idCompeticion);

        for (Match match : matchList) {
            match.setCompetition(competition);
            match.setTeamLocal(teamDAO.findById(match.getIdTeamLocal()));
            match.setTeamVisitor(teamDAO.findById(match.getIdTeamVisitor()));
            match.setPlace(placeDAO.findById(match.getIdPlace()));
        }
        return  matchList;
    }
}
