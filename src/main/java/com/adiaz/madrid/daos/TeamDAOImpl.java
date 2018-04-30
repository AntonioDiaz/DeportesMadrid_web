package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Team;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import com.googlecode.objectify.Key;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class TeamDAOImpl implements TeamDAO {



    @Override
    public Key<Team> create(Team item) throws Exception {
        return ofy().save().entity(item).now();
    }

    @Override
    public boolean update(Team item) throws Exception {
        boolean updateResult;
        if (item == null || item.getId() == null) {
            updateResult = false;
        } else {
            Team c = ofy().load().type(Team.class).id(item.getId()).now();
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
    public void remove(Long id) throws Exception {
        ofy().delete().type(Team.class).id(id).now();
    }

    @Override
    public Team findById(Long id) {
        if (id==0) {
            return null;
        }
        Team team = ofy().load().type(Team.class).id(id).now();
        return team;
    }

    @Override
    public List<Team> findAll() {
        return ofy().load().type(Team.class).list();
    }

    @Override
    public List<Team> findByName(String name) {
        return ofy().load().type(Team.class).filter("name >=", name).filter("name <=", name + "\uFFFD").list();
    }

    @Override
    public void insertList(Collection<Team> teamList) {
        ofy().save().entities(teamList);
    }

    @Override
    public List<Team> findTeamsPagination(int page) {
        int from = page * DeportesMadridConstants.PAGINATION_RECORDS;
        return ofy().load().type(Team.class).limit(DeportesMadridConstants.PAGINATION_RECORDS).offset(from).list();
    }
}
