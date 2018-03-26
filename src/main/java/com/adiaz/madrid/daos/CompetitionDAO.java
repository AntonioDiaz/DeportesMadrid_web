package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Competition;

import java.util.Collection;
import java.util.List;

public interface CompetitionDAO extends GenericDAO<Competition, String> {


    void insertList(Collection<Competition> competitions) throws Exception;

    List<Competition> distinctTemporadas();

    List<Competition> distinctCodCompetiticiones(Integer codTemporada);

    List<Competition> distinctFases(Integer codTemporada, String codCompeticion);

    List<Competition> distinctGrupos(Integer codTemporada, String codCompeticion, Integer codFase);

    List<Competition> distinctSports();

    List<Competition> distinctDistritos(String sport);
}
