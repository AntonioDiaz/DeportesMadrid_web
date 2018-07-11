package com.adiaz.madrid.services;


import com.adiaz.madrid.daos.GroupDAO;
import com.adiaz.madrid.entities.Group;
import com.adiaz.madrid.entities.Team;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import com.adiaz.madrid.utils.entities.SportsCountEntity;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Service ("GroupManager")
public class GroupManagerImpl implements GroupManager {

    @Autowired
    GroupDAO groupDAO;

    @Override
    public Integer countGroups() {
        return findAllGroups().size();
    }

    @Override
    public List<Group> findAllGroups() {
        List<Group> groups;
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        groups = (List<Group>) syncCache.get(DeportesMadridConstants.CACHE_GROUPS_LIST);
        if (groups==null) {
            groups = groupDAO.findAll();
            syncCache.put(DeportesMadridConstants.CACHE_GROUPS_LIST, groups);
        }
        return groups;
    }

    @Override
    public List<Group> findGroups(String groupName) {
        List<Group> groupList = new ArrayList<>();
        for (Group group : findAllGroups()) {
            if (group.getNombreGrupo().contains(groupName)) {
                groupList.add(group);
            }
        }
        return groupList;
    }

    @Override
    public List<Group> findGroups(Integer codTemporada) {
        return groupDAO.findByCodTemporada(codTemporada);
    }

    @Override
    public Group findById(String id) {
        return groupDAO.findById(id);
    }

    @Override
    public List<Integer> distinctTemporadas() {
        List<Integer> temporadasList = new ArrayList<>();
        List<Group> groupList = groupDAO.distinctGroupsTemporada();
        for (Group group : groupList) {
            temporadasList.add(group.getCodTemporada());
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
        for (Group group : groupDAO.distinctSports()) {
            sportsList.add(group.getDeporte());
        }
        return sportsList;
    }

    @Override
    public List<String> distinctDistritos(String sport) {
        List<String> distritosList = new ArrayList<>();
        List<Group> groupList = groupDAO.distinctDistritos(sport);
        for (Group group : groupList) {
            distritosList.add(group.getDistrito());
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
