package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Team;

import java.util.Collection;
import java.util.List;

public interface TeamDAO extends GenericDAO<Team, Long> {
    List<Team> findByName(String name);
    void insertList(Collection<Team> teamList);
}
