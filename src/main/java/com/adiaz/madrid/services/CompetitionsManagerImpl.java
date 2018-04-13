package com.adiaz.madrid.services;


import com.adiaz.madrid.daos.CompetitionDAO;
import com.adiaz.madrid.entities.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service ("CompetitionManager")
public class CompetitionsManagerImpl implements CompetitionsManager {

    @Autowired
    CompetitionDAO competitionDAO;

    @Override
    public Integer competitionsCount() {
        return competitionDAO.findAll().size();
    }

    @Override
    public List<Competition> findAllCompetitions() {
        return competitionDAO.findAll();
    }

    @Override
    public List<Competition> findCompetitions(String groupName) {
        List<Competition> competitionList = new ArrayList<>();
        for (Competition competition : competitionDAO.findAll()) {
            if (competition.getNombreGrupo().contains(groupName)) {
                competitionList.add(competition);
            }
        }
        return competitionList;
    }

    @Override
    public Competition findById(String id) {
        return competitionDAO.findById(id);
    }

    @Override
    public List<Integer> distinctTemporadas() {
        List<Integer> temporadasList = new ArrayList<>();
        List<Competition> competitionList = competitionDAO.distinctTemporadas();
        for (Competition competition : competitionList) {
            temporadasList.add(competition.getCodTemporada());
        }
        return temporadasList;
    }

    @Override
    public List<Competition> distinctCompeticion(Integer temporada) {
        return competitionDAO.distinctCodCompetiticiones(temporada);
    }

    @Override
    public List<Competition> distinctFase(Integer temporada, String competicion) {
        return competitionDAO.distinctFases(temporada, competicion);
    }

    @Override
    public List<Competition> distinctGrupo(Integer temporada, String competicion, Integer fase) {
        return competitionDAO.distinctGrupos(temporada, competicion, fase);
    }

    @Override
    public List<String> distinctSports() {
        List <String> sportsList = new ArrayList<>();
        for (Competition competition : competitionDAO.distinctSports()) {
            sportsList.add(competition.getDeporte());
        }
        return sportsList;
    }

    @Override
    public List<String> distinctDistritos(String sport) {
        List<String> distritosList = new ArrayList<>();
        List<Competition> competitionList = competitionDAO.distinctDistritos(sport);
        for (Competition competition : competitionList) {
            distritosList.add(competition.getDistrito());
        }
        return distritosList;
    }
}
