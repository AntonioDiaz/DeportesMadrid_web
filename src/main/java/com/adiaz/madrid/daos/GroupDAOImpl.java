package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Group;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class GroupDAOImpl implements GroupDAO {

    @Override
    public Key<Group> create(Group item) throws Exception {
        item.setId(DeportesMadridUtils.generateIdGroup(item));
        return ofy().save().entity(item).now();
    }

    @Override
    public boolean update(Group item) throws Exception {
        boolean updateResult;
        if (item == null || item.getId() == null) {
            updateResult = false;
        } else {
            Group c = ofy().load().type(Group.class).id(item.getId()).now();
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
        ofy().delete().type(Group.class).id(id);
    }

    @Override
    public Group findById(String id) {
        return ofy().load().type(Group.class).id(id).now();
    }

    @Override
    public List<Group> findAll() {
        return ofy().load().type(Group.class).list();
    }

    @Override
    public void insertList(Collection<Group> groups) throws Exception {
        ofy().save().entities(groups);
    }

    @Override
    public List<Group> distinctGroupsTemporada() {
        Query<Group> codTemporada =  ofy().load().type(Group.class).project("codTemporada").distinct(true);
        List<Group> list = codTemporada.list();
        return list;
    }

    @Override
    public List<Group> distinctGroupsCodCompetiticiones(Integer codTemporada) {
        Query<Group> query = ofy().load().type(Group.class).project("codCompeticion").distinct(true);
        List<Group> groupsList = query.filter("codTemporada", codTemporada).list();
        return groupsList;
    }

    @Override
    public List<Group> distinctGroupFases(Integer codTemporada, String codCompeticion) {
        Query<Group> query = ofy().load().type(Group.class).project("codFase").distinct(true);
        List<Group> groupsList = query
                .filter("codTemporada", codTemporada)
                .filter("codCompeticion", codCompeticion)
                .list();
        return groupsList;
    }

    @Override
    public List<Group> distinctGroupCodGrupo(Integer codTemporada, String codCompeticion, Integer codFase) {
        Query<Group> query = ofy().load().type(Group.class).project("codGrupo").distinct(true);
        List<Group> groupsList = query
                .filter("codTemporada", codTemporada)
                .filter("codCompeticion", codCompeticion)
                .filter("codFase", codFase)
                .list();
        return groupsList;
    }

    @Override
    public List<Group> distinctSports() {
        return ofy().load().type(Group.class).project("deporte").distinct(true).list();
    }

    @Override
    public List<Group> distinctDistritos(String sport) {
        Query<Group> query = ofy().load().type(Group.class).project("distrito").distinct(true);
        List<Group> groupsList = query
                .filter("deporte", sport)
                .list();
        return groupsList;
    }

    @Override
    public List<Group> findBySport(String sport) {
        return ofy().load().type(Group.class).filter("deporte", sport).list();
    }

    @Override
    public List<Group> findByCodTemporada(Integer codTemporada) {
        return ofy().load().type(Group.class).filter("codTemporada", codTemporada).list();
    }
}
