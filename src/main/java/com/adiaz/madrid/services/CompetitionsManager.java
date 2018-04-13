package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.Competition;

import java.util.List;

public interface CompetitionsManager {


    Integer competitionsCount();
    List<Competition> findAllCompetitions();
    List<Competition> findCompetitions(String groupName);
    Competition findById(String id);
    List<Integer> distinctTemporadas();
    List<Competition> distinctCompeticion(Integer temporada);
    List<Competition> distinctFase(Integer temporada, String competicion);
    List<Competition> distinctGrupo(Integer temporada, String competicion, Integer fase);
    List<String> distinctSports();
    List<String> distinctDistritos(String sport);

}
