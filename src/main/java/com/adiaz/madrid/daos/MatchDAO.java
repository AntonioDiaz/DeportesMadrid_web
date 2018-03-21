package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Competition;
import com.adiaz.madrid.entities.Match;
import com.googlecode.objectify.Ref;

import java.util.Collection;
import java.util.List;

public interface MatchDAO extends GenericDAO<Match, String> {
    int recordsCount();
    List<Match> findByCompeticion(String idCompeticion);
    void insertList(Collection<Match> matchList);
}
