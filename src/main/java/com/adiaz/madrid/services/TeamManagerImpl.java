package com.adiaz.madrid.services;

import com.adiaz.madrid.controllers.ReleaseController;
import com.adiaz.madrid.daos.MatchDAO;
import com.adiaz.madrid.daos.TeamDAO;
import com.adiaz.madrid.entities.Match;
import com.adiaz.madrid.entities.Team;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Level;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;


@Service ("teamManager")
public class TeamManagerImpl implements TeamManager {

    public static final Logger logger = Logger.getLogger(TeamManagerImpl.class);


    @Autowired
    MatchDAO matchDAO;

    @Autowired
    TeamDAO teamDAO;

    @Override
    public int teamsCount() {
        return findAll().size();
    }

    @Override
    public List<Team> findTeams(String teamName) {
        List<Team> teams = new ArrayList<>();
        String teamNameNormalized = teamName.replace("Ñ", "\001");
        teamNameNormalized = StringUtils.stripAccents(teamNameNormalized);
        teamNameNormalized = teamNameNormalized.replace("\001", "Ñ");
        for (Team team : findAll()) {
            if (team.getName().contains(teamName) || team.getName().contains(teamNameNormalized)) {
                teams.add(team);
            }
        }
        return teams;
    }

    @Override
    public List<Team> findAll() {
        List<Team> all;
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        all = (List<Team>) syncCache.get(DeportesMadridConstants.CACHE_TEAMS_LIST);
        if (all==null) {
            all = teamDAO.findAll();
            syncCache.put(DeportesMadridConstants.CACHE_TEAMS_LIST, all);
        }
        return all;
    }

    /*
    @Override
    public Set<String> teamGroups(Long idTeam) {
        Set<String> idGroups = new HashSet<>();
        List<Match> groupsOfATeam = matchDAO.findGroupsOfATeam(idTeam);
        for (Match match : groupsOfATeam) {
            idGroups.add(match.getIdGroup());
        }
        return idGroups;
    }

    // TODO: 30/4/18 remove this.
    @Override
    public void updateTeamsGroups() {
        Integer pageMatches = 0;
        List<Match> matchesPagination = matchDAO.findMatchesPagination(pageMatches);
        logger.debug("Creating teams and groups map. " + pageMatches);
        while (matchesPagination.size() > 0) {
            for (Match match : matchesPagination) {
                //addGroup(mapTeamsGroups, match.getIdTeamLocal(), match.getIdGroup());
                //addGroup(mapTeamsGroups, match.getIdTeamVisitor(), match.getIdGroup());
            }
            pageMatches++;
            logger.debug("Creating teams and groups map. " + pageMatches);
            matchesPagination = matchDAO.findMatchesPagination(pageMatches);
        }
    }
    */

    /*
    public void updateTeamsGroups() {
        //map<idTeam, groups of this team>
        Map<Long, Set<String>> mapTeamsGroups = new HashMap<>();
        Integer pageMatches = 0;
        List<Match> matchesPagination = matchDAO.findMatchesPagination(pageMatches);
        logger.debug("Creating teams and groups map. " + pageMatches);
        while (matchesPagination.size() > 0) {
            for (Match match : matchesPagination) {
                addGroup(mapTeamsGroups, match.getIdTeamLocal(), match.getIdGroup());
                addGroup(mapTeamsGroups, match.getIdTeamVisitor(), match.getIdGroup());
            }
            pageMatches++;
            logger.debug("Creating teams and groups map. " + pageMatches);
            matchesPagination = matchDAO.findMatchesPagination(pageMatches);
        }
        Integer pageTeams = 0;
        List<Team> teamsList = teamDAO.findTeamsPagination(pageTeams);
        List<Team> teamsToInsert;
        while (teamsList.size() > 0) {
            teamsToInsert = new ArrayList<>();
            for (Team team : teamsList) {
                if (!team.getGroups().equals(mapTeamsGroups.get(team.getId()))) {
                    team.setGroups(mapTeamsGroups.get(team.getId()));
                    teamsToInsert.add(team);
                }
            }
            teamDAO.insertList(teamsToInsert);
            logger.debug("updateTeamsGroups teamsToInsert -->" + teamsToInsert.size());
            pageTeams++;
            teamsList = teamDAO.findTeamsPagination(pageTeams);
        }
    }

    private void addGroup(Map<Long, Set<String>> mapTeamsGroups, Long teamId, String idGroup) {
        Set<String> setGroups;
        if (mapTeamsGroups.containsKey(teamId)) {
            setGroups = mapTeamsGroups.get(teamId);
        } else {
            setGroups = new HashSet<>();
        }
        setGroups.add(idGroup);
        mapTeamsGroups.put(teamId, setGroups);
    }
    */
}
