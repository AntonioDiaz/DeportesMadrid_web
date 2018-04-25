package com.adiaz.madrid.services;


import com.adiaz.madrid.daos.GroupDAO;
import com.adiaz.madrid.entities.Group;
import com.adiaz.madrid.utils.entities.SportsCountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service ("GroupManager")
public class GroupManagerImpl implements GroupManager {

    @Autowired
    GroupDAO groupDAO;

    @Override
    public Integer countGroups() {
        return groupDAO.findAll().size();
    }

    @Override
    public List<Group> findAllGroups() {
        return groupDAO.findAll();
    }

    @Override
    public List<Group> findGroups(String groupName) {
        List<Group> groupList = new ArrayList<>();
        for (Group group : groupDAO.findAll()) {
            if (group.getNombreGrupo().contains(groupName)) {
                groupList.add(group);
            }
        }
        return groupList;
    }

    @Override
    public Group findById(String id) {
        return groupDAO.findById(id);
    }

    @Override
    public List<Integer> distinctTemporadas() {
        List<Integer> temporadasList = new ArrayList<>();
        List<Group> groupList = groupDAO.distinctGroupsTemporada();
        for (Group competition : groupList) {
            temporadasList.add(competition.getCodTemporada());
        }
        return temporadasList;
    }

    @Override
    public List<Group> distinctGroups(Integer temporada) {
        return groupDAO.distinctGroupsCodCompetiticiones(temporada);
    }

    @Override
    public List<Group> distinctGroups(Integer temporada, String competicion) {
        return groupDAO.distinctGroupFases(temporada, competicion);
    }

    @Override
    public List<Group> distinctGroups(Integer temporada, String competicion, Integer fase) {
        return groupDAO.distinctGroupCodGrupo(temporada, competicion, fase);
    }

    @Override
    public List<String> distinctSports() {
        List <String> sportsList = new ArrayList<>();
        for (Group competition : groupDAO.distinctSports()) {
            sportsList.add(competition.getDeporte());
        }
        return sportsList;
    }

    @Override
    public List<String> distinctDistritos(String sport) {
        List<String> distritosList = new ArrayList<>();
        List<Group> competitionList = groupDAO.distinctDistritos(sport);
        for (Group competition : competitionList) {
            distritosList.add(competition.getDistrito());
        }
        return distritosList;
    }

    @Override
    public List<SportsCountEntity> distinctSportsCount() {
        List<SportsCountEntity> sportsCountEntityList = new ArrayList<>();
        for (String s : distinctSports()) {
            List<Group> bySport = groupDAO.findBySport(s);
            sportsCountEntityList.add(new SportsCountEntity (s, bySport.size()));
        }
        return sportsCountEntityList;
    }
}
