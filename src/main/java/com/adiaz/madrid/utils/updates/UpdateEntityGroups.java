package com.adiaz.madrid.utils.updates;

import com.adiaz.madrid.daos.GroupDAO;
import com.adiaz.madrid.daos.ReleaseDAO;
import com.adiaz.madrid.entities.Group;
import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.adiaz.madrid.utils.entities.MatchLineEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class UpdateEntityGroups extends UpdateEntityAbstract<Group> {

    private static final Logger logger = Logger.getLogger(UpdateEntityGroups.class);

    @Autowired
    ReleaseDAO releaseDAO;

    @Autowired
    GroupDAO groupDAO;

    @Override
    int getLinesProcessed(Release release) {
        return release.getLinesTeamsGroups();
    }

    @Override
    void addOneLineError(Release release) throws Exception {
        release.setLinesGroupsErrors(release.getLinesTeamsGroups() + 1);
        releaseDAO.update(release);
    }

    @Override
    void addEntityToMap(Map map, String line) throws Exception {
        MatchLineEntity matchLineEntity = new MatchLineEntity(line);
        Group group = new Group();
        Integer codTemporada = matchLineEntity.getField00_codTemporada();
        String codCompeticion = matchLineEntity.getField01_codCompeticion();
        Integer codFase = matchLineEntity.getField02_codFase();
        Integer codGrupo = matchLineEntity.getField03_codGrupo();
        String idGroup = DeportesMadridUtils.generateIdGroup(codTemporada, codCompeticion, codFase, codGrupo);
        group.setCodTemporada(codTemporada);
        group.setCodCompeticion( codCompeticion);
        group.setCodFase(codFase);
        group.setCodGrupo(codGrupo);
        group.setId(idGroup);
        group.setNombreTemporada(matchLineEntity.getField15_nombreTemporada());
        group.setNombreCompeticion(matchLineEntity.getField16_nombreCompeticion());
        group.setNombreFase(matchLineEntity.getField17_nombreFase());
        group.setNombreGrupo(matchLineEntity.getField18_nombreGrupo());
        group.setDeporte(matchLineEntity.getField19_nombreDeporte());
        group.setCategoria(matchLineEntity.getField20_nombreCategoria());
        group.setDistrito(matchLineEntity.getField26_distrito());
        Group groupOriginal = groupDAO.findById(idGroup);
        if (groupOriginal==null || !groupOriginal.equals(group)) {
            map.put(group.getId(), group);
        }
    }

    @Override
    void insertEntities(Release release, Collection<Group> entitiesToUpdate, int linesUpdated, boolean ended) throws Exception {
        groupDAO.insertList(entitiesToUpdate);
        release.setLinesGroups(linesUpdated);
        release.setUpdatedGroups(ended);
        releaseDAO.update(release);
        logger.debug("Groups current line:" + linesUpdated + " records updated: " + entitiesToUpdate.size());
    }
}
