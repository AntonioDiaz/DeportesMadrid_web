package com.adiaz.madrid.utils.updates;

import com.adiaz.madrid.daos.ReleaseDAO;
import com.adiaz.madrid.daos.TeamDAO;
import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.entities.Team;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.adiaz.madrid.utils.entities.MatchLineEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class UpdateEntityTeamsGroups extends UpdateEntityAbstract<Team> {

    @Autowired
    ReleaseDAO releaseDAO;

    @Autowired
    TeamDAO teamDAO;

    @Override
    int getLinesProcessed(Release release) {
        return release.getLinesTeamsGroups();
    }

    @Override
    void addOneLineError(Release release) throws Exception {
        release.setLinesTeamsGroupsErrors(release.getLinesTeamsGroupsErrors() + 1);
    }

    @Override
    void addEntityToMap(Map map, String line) throws Exception {
        MatchLineEntity lineEntity = new MatchLineEntity(line);
        Integer codTemporada = lineEntity.getField00_codTemporada();
        String codCompeticion = lineEntity.getField01_codCompeticion();
        Integer codFase = lineEntity.getField02_codFase();
        Integer codGrupo = lineEntity.getField03_codGrupo();
        Long teamId = lineEntity.getField06_codEquipoLocal();
        if (teamId!=null && teamId!=0) {
            String idGroup = DeportesMadridUtils.generateIdGroup(codTemporada, codCompeticion, codFase, codGrupo);
            Team teamLocal = teamDAO.findById(teamId);
            if (teamLocal!=null && !map.containsKey(teamLocal.getId()) && !teamLocal.getGroups().contains(idGroup)) {
                teamLocal.getGroups().add(idGroup);
                map.put(teamLocal.getId(), teamLocal);
            }
        }
    }

    @Override
    void insertEntities(Release release, Collection<Team> entitiesToUpdate, int linesUpdated, boolean ended) throws Exception {
        teamDAO.insertList(entitiesToUpdate);
        release.setLinesTeamsGroups(linesUpdated);
        release.setUpdatedTeamsGroups(ended);
        releaseDAO.update(release);
    }
}
