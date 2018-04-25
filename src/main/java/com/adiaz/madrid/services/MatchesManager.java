package com.adiaz.madrid.services;


import com.adiaz.madrid.entities.Match;

import java.util.List;

public interface MatchesManager {

    int matchesCount();
    List<Match> findMatchesByIdGroup(String idGroup);
}
