package com.adiaz.madrid.utils.updates;

import com.adiaz.madrid.daos.ClassificationDAO;
import com.adiaz.madrid.daos.ReleaseDAO;
import com.adiaz.madrid.entities.ClassificationEntry;
import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.adiaz.madrid.utils.entities.ClassificationLineEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class UpdateEntityClassification extends UpdateEntityAbstract<ClassificationEntry> {

    private static final Logger logger = Logger.getLogger(UpdateEntityClassification.class);


    @Autowired
    ReleaseDAO releaseDAO;

    @Autowired
    ClassificationDAO classificationDAO;

    @Override
    int getLinesProcessed(Release release) {
        return release.getLinesClassification();
    }

    @Override
    void addOneLineError(Release release) throws Exception {
        release.setLinesClassificationErrors(release.getLinesClassificationErrors() + 1);
        releaseDAO.update(release);
    }

    @Override
    void addEntityToMap(Map map, String line) throws Exception {
        ClassificationLineEntity classificationLineEntity = new ClassificationLineEntity(line);
        Integer codTemporada = classificationLineEntity.getField00_codTemporada();
        String codCompeticion = classificationLineEntity.getField01_codCompeticion();
        Integer codFase = classificationLineEntity.getField02_codFase();
        Integer codGrupo = classificationLineEntity.getField03_codGrupo();
        Long codEquipo = classificationLineEntity.getField04_codEquipo();
        String idGroup = DeportesMadridUtils.generateIdGroup(codTemporada, codCompeticion, codFase, codGrupo);
        String idClassificationEntry = DeportesMadridUtils.generateIdClassification(codTemporada, codCompeticion, codFase, codGrupo, codEquipo);
        ClassificationEntry classificationEntry = new ClassificationEntry();
        classificationEntry.setIdGroup(idGroup);
        classificationEntry.setId(idClassificationEntry);
        classificationEntry.setIdTeam(codEquipo);
        classificationEntry.setPosition(classificationLineEntity.getField05_posicion());
        classificationEntry.setPoints(classificationLineEntity.getField06_puntos());
        classificationEntry.setMatchesPlayed(classificationLineEntity.getField07_partidosJugados());
        classificationEntry.setMatchesWon(classificationLineEntity.getField08_partidosGanados());
        classificationEntry.setMatchesDrawn(classificationLineEntity.getField09_partidosEmpatados());
        classificationEntry.setMatchesLost(classificationLineEntity.getField10_partidosPerdidos());
        classificationEntry.setPointsFavor(classificationLineEntity.getField11_golesFavor());
        classificationEntry.setPointsAgainst(classificationLineEntity.getField12_golesContra());
        ClassificationEntry classificationEntryOriginal = classificationDAO.findById(idClassificationEntry);
        if (classificationEntryOriginal==null || !classificationEntry.equals(classificationEntryOriginal)) {
            map.put(idClassificationEntry, classificationEntry);
        }
    }

    @Override
    void insertEntities(Release release, Collection<ClassificationEntry> entitiesToUpdate, int linesUpdated, boolean ended) throws Exception {
        classificationDAO.insertList(entitiesToUpdate);
        release.setLinesClassification(linesUpdated);
        release.setUpdatedClassification(ended);
        releaseDAO.update(release);
        logger.debug("Classification current line:" + linesUpdated + " records updated: " + entitiesToUpdate.size());
    }
}
