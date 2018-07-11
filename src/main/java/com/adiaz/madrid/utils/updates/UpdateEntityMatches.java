package com.adiaz.madrid.utils.updates;

import com.adiaz.madrid.daos.MatchDAO;
import com.adiaz.madrid.daos.ReleaseDAO;
import com.adiaz.madrid.entities.Match;
import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.adiaz.madrid.utils.entities.MatchLineEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class UpdateEntityMatches extends UpdateEntityAbstract<Match> {

    private static final Logger logger = Logger.getLogger(UpdateEntityMatches.class);


    @Autowired
    ReleaseDAO releaseDAO;

    @Autowired
    MatchDAO matchDAO;

    @Override
    int getLinesProcessed(Release release) {
        return release.getLinesMatches();
    }

    @Override
    void addOneLineError(Release release) throws Exception {
        release.setLinesMatchesErrors(release.getLinesMatchesErrors() + 1);
        releaseDAO.update(release);
    }

    @Override
    void addEntityToMap(Map map, String line) throws Exception {
        MatchLineEntity matchLineEntity = new MatchLineEntity(line);
        Match match = new Match();
        //find group
        Integer codTemporada = matchLineEntity.getField00_codTemporada();
        String codCompeticion = matchLineEntity.getField01_codCompeticion();
        Integer codFase = matchLineEntity.getField02_codFase();
        Integer codGrupo = matchLineEntity.getField03_codGrupo();
        Integer weekNumber = matchLineEntity.getField04_weekNum();
        Integer matchNumber = matchLineEntity.getField05_matchNum();
        String idGroup = DeportesMadridUtils.generateIdGroup(codTemporada, codCompeticion, codFase, codGrupo);
        String idMatch = DeportesMadridUtils.generateIdMatch(codTemporada, codCompeticion, codFase, codGrupo, weekNumber, matchNumber);
        String dateStr = matchLineEntity.getField11_fecha() + " " + matchLineEntity.getField12_hora();
        match.setId(idMatch);
        match.setIdGroup(idGroup);
        match.setIdTeamLocal(matchLineEntity.getField06_codEquipoLocal());
        match.setIdTeamVisitor(matchLineEntity.getField07_codEquipoVisitante());
        match.setIdPlace(matchLineEntity.getField10_codCampo());
        match.setScoreLocal(matchLineEntity.getField08_scoreLocal());
        match.setScoreVisitor(matchLineEntity.getField09_scoreVisitor());
        match.setDate(DeportesMadridUtils.stringToDate(dateStr));
        match.setNumWeek(matchLineEntity.getField04_weekNum());
        match.setNumMatch(matchLineEntity.getField05_matchNum());
        match.setScheduled(matchLineEntity.getField13_programado()==1);
        match.setState(calculateState(matchLineEntity));
        Match matchOriginal = matchDAO.findById(idMatch);

        if (matchOriginal==null || !matchOriginal.equals(match)) {
            map.put(match.getId(), match);
            /*
            if (match.getTeamLocal()!=null) {
                teamsUpdated.add(new String[]{match.getIdTeamLocal().toString(), idGroup});
            }
            if (match.getTeamVisitor()!=null) {
                teamsUpdated.add(new String[]{match.getTeamVisitor().toString(), idGroup});
            }
            */
        }
    }

    @Override
    void insertEntities(Release release, Collection<Match> entitiesToUpdate, int linesUpdated, boolean ended) throws Exception {
        matchDAO.insertList(entitiesToUpdate);
        release.setLinesMatches(linesUpdated);
        release.setUpdatedMatches(ended);
        releaseDAO.update(release);
        logger.debug("Matches current line:" + linesUpdated + " records updated: " + entitiesToUpdate.size());
    }

    private Integer calculateState(MatchLineEntity matchLineEntity) {
        Integer state = DeportesMadridConstants.MATCH_STATE.PENDIENTE.getValue();
        if ((matchLineEntity.getField06_codEquipoLocal()==0 && matchLineEntity.getField07_codEquipoVisitante()!=0)
                || (matchLineEntity.getField06_codEquipoLocal()!=0 && matchLineEntity.getField07_codEquipoVisitante()==0)) {
            state = DeportesMadridConstants.MATCH_STATE.DESCANSA.getValue();
        } else {
            if (StringUtils.isNotEmpty(matchLineEntity.getField14_estado())) {
                state = DeportesMadridConstants.MATCH_STATE.createState(matchLineEntity.getField14_estado().charAt(0)).getValue();
            }
        }
        return state;
    }
}
