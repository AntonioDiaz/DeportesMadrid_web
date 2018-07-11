package com.adiaz.madrid.utils.updates;

import com.adiaz.madrid.daos.ReleaseDAO;
import com.adiaz.madrid.daos.TeamDAO;
import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.entities.Team;
import com.adiaz.madrid.utils.entities.MatchLineEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class UpdateEntityTeams extends UpdateEntityAbstract<Team> {

    private static final Logger logger = Logger.getLogger(UpdateEntityTeams.class);

    @Autowired
    ReleaseDAO releaseDAO;

    @Autowired
    TeamDAO teamDAO;

    @Override
    int getLinesProcessed(Release release) {
        return release.getLinesTeams();
    }

    @Override
    void addOneLineError(Release release) throws Exception {
        release.setLinesTeamsErrors(release.getLinesTeamsErrors()+1);
        releaseDAO.update(release);
    }

    @Override
    void addEntityToMap(Map map, String line) throws Exception {
        MatchLineEntity lineEntity = new MatchLineEntity(line);
        Team teamLocal = addOrUpdateTeam(lineEntity.getField06_codEquipoLocal(), lineEntity.getField22_equipoLocal());
        if (teamLocal != null) {
            map.put(teamLocal.getId(), teamLocal);
        }
        Team teamVisitor = addOrUpdateTeam(lineEntity.getField07_codEquipoVisitante(), lineEntity.getField23_equipoVisitante());
        if (teamVisitor != null) {
            map.put(teamVisitor.getId(), teamVisitor);
        }
    }

    @Override
    void insertEntities(Release release, Collection<Team> entitiesToUpdate, int linesUpdated, boolean ended) throws Exception {
        logger.debug("Teams current line:" + linesUpdated + " records updated: " + entitiesToUpdate.size());
        teamDAO.insertList(entitiesToUpdate);
        release.setLinesTeams(linesUpdated);
        release.setUpdatedTeams(ended);
        releaseDAO.update(release);
    }

    private Team addOrUpdateTeam(Long teamId, String teamName) {
        if (teamId!=null && teamId!=0 && StringUtils.isNotBlank(teamName)) {
            Team teamNew = new Team();
            teamNew.setId(teamId);
            teamNew.setName(teamName);
            Team teamOriginal = teamDAO.findById(teamId);
            if (teamOriginal==null || !teamOriginal.getName().equals(teamNew.getName())) {
                return teamNew;
            }
        }
        return null;
    }
}
