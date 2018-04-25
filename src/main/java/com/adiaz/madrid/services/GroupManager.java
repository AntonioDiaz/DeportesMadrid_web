package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.Group;
import com.adiaz.madrid.utils.entities.SportsCountEntity;

import java.util.List;

public interface GroupManager {
    Integer countGroups();
    List<Group> findAllGroups();
    List<Group> findGroups(String groupName);
    Group findById(String id);
    List<Integer> distinctTemporadas();
    List<Group> distinctGroups(Integer temporada);
    List<Group> distinctGroups(Integer temporada, String competicion);
    List<Group> distinctGroups(Integer temporada, String competicion, Integer fase);
    List<String> distinctSports();
    List<String> distinctDistritos(String sport);
    List<SportsCountEntity> distinctSportsCount();

}
