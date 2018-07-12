package com.adiaz.madrid.utils.updates;

import com.adiaz.madrid.daos.PlaceDAO;
import com.adiaz.madrid.daos.ReleaseDAO;
import com.adiaz.madrid.entities.Place;
import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.utils.entities.MatchLineEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Component
public class UpdateEntityPlaces extends UpdateEntityAbstract <Place> {

    private static final Logger logger = Logger.getLogger(UpdateEntityPlaces.class);

    @Autowired
    PlaceDAO placeDAO;

    @Autowired
    ReleaseDAO releaseDAO;


    @Override
    int getLinesProcessed(Release release) {
        return release.getLinesPlaces();
    }

    @Override
    void addOneLineError(Release release) throws Exception {
        release.setLinesPlacesErrors(release.getLinesPlacesErrors() + 1);
        releaseDAO.update(release);

    }

    @Override
    Set<String[]> addEntityToMap(Map map, String line) throws Exception {
        MatchLineEntity lineEntity = new MatchLineEntity(line);
        Place place = new Place();
        Long id = lineEntity.getField10_codCampo();
        if (id!=0) {
            Place placeOriginal = placeDAO.findById(id);
            place.setId(id);
            place.setName(lineEntity.getField24_campo());
            place.setCoordX(lineEntity.getField29_coordx());
            place.setCoordY(lineEntity.getField30_coordy());
            if (placeOriginal==null || !placeOriginal.equals(place)) {
                map.put(place.getId(), place);
            }
        }
        return null;
    }

    @Override
    void insertEntities(Release release, Collection<Place> entitiesToUpdate, int linesUpdated, boolean ended) throws Exception {
        placeDAO.insertList(entitiesToUpdate);
        release.setLinesPlaces(linesUpdated);
        release.setUpdatedPlaces(ended);
        releaseDAO.update(release);
        logger.debug("Places current line:" + linesUpdated + " records updated: " + entitiesToUpdate.size());
    }
}
