package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Match;
import com.adiaz.madrid.entities.Team;

import java.util.Collection;
import java.util.List;

public interface MatchDAO extends GenericDAO<Match, String> {
    int recordsCount();
    List<Match> findByCompeticion(String idCompeticion);
    void insertList(Collection<Match> matchList);
    List<Match> findGroupsOfATeam(Long idTeam);
    List<Match> findMatchesPagination(int page);

}
