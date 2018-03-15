package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.MatchDAO;
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

    @Override
    public int matchesCount() {
        return matchDAO.recordsCount();
    }

    @Override
    public List<Match> findMatchesByCompetition(String idCompeticion) {
        return matchDAO.findByCompeticion(idCompeticion);
    }
}