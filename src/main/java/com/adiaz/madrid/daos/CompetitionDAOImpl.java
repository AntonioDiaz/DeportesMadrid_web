package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Competition;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class CompetitionDAOImpl implements CompetitionDAO {

    @Override
    public Key<Competition> create(Competition item) throws Exception {
        item.setId(DeportesMadridUtils.generateIdCompetition(item));
        return ofy().save().entity(item).now();
    }

    @Override
    public boolean update(Competition item) throws Exception {
        boolean updateResult;
        if (item == null || item.getId() == null) {
            updateResult = false;
        } else {
            Competition c = ofy().load().type(Competition.class).id(item.getId()).now();
            if (c != null) {
                ofy().save().entity(item).now();
                updateResult = true;
            } else {
                updateResult = false;
            }
        }
        return updateResult;
    }

    @Override
    public void remove(String id) throws Exception {
        ofy().delete().type(Competition.class).id(id);
    }

    @Override
    public Competition findById(String id) {
        return ofy().load().type(Competition.class).id(id).now();
    }

    @Override
    public List<Competition> findAll() {
        return ofy().load().type(Competition.class).list();
    }

    // TODO: 19/3/18 remove this method.
    @Override
    public Competition findCompetition(String idCompetition) {
        Key<Competition> key = Key.create(Competition.class, idCompetition);
        return ofy().load().key(key).now();
    }

    @Override
    public void insertList(Collection<Competition> competitions) throws Exception {
        ofy().save().entities(competitions);
    }

    @Override
    public List<Competition> distinctTemporadas() {
        Query<Competition> codTemporada =  ofy().load().type(Competition.class).project("codTemporada").distinct(true);
        List<Competition> list = codTemporada.list();
        return list;
    }

    @Override
    public List<Competition> distinctCodCompetiticiones(Integer codTemporada) {
        Query<Competition> query = ofy().load().type(Competition.class).project("codCompeticion").distinct(true);
        List<Competition> competitionList = query.filter("codTemporada", codTemporada).list();
        return competitionList;
    }

    @Override
    public List<Competition> distinctFases(Integer codTemporada, String codCompeticion) {
        Query<Competition> query = ofy().load().type(Competition.class).project("codFase").distinct(true);
        List<Competition> competitionList = query
                .filter("codTemporada", codTemporada)
                .filter("codCompeticion", codCompeticion)
                .list();
        return competitionList;
    }

    @Override
    public List<Competition> distinctGrupos(Integer codTemporada, String codCompeticion, Integer codFase) {
        Query<Competition> query = ofy().load().type(Competition.class).project("codGrupo").distinct(true);
        List<Competition> competitionList = query
                .filter("codTemporada", codTemporada)
                .filter("codCompeticion", codCompeticion)
                .filter("codFase", codFase)
                .list();
        return competitionList;
    }

    @Override
    public List<Competition> distinctSports() {
        return ofy().load().type(Competition.class).project("deporte").distinct(true).list();
    }

    @Override
    public List<Competition> distinctDistritos(String sport) {
        Query<Competition> query = ofy().load().type(Competition.class).project("distrito").distinct(true);
        List<Competition> competitionList = query
                .filter("deporte", sport)
                .list();
        return competitionList;
    }
}
