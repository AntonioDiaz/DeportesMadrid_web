package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Group;

import java.util.Collection;
import java.util.List;

public interface GroupDAO extends GenericDAO<Group, String> {

    void insertList(Collection<Group> groups) throws Exception;

    List<Group> distinctGroupsTemporada();

    List<Group> distinctGroupsCodCompetiticiones(Integer codTemporada);

    List<Group> distinctGroupFases(Integer codTemporada, String codCompeticion);

    List<Group> distinctGroupCodGrupo(Integer codTemporada, String codCompeticion, Integer codFase);

    List<Group> distinctSports();

    List<Group> distinctDistritos(String sport);

    List<Group> findBySport(String sport);

    List<Group> findByCodTemporada(Integer codTemporada);
}
