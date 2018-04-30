package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Group;
import com.adiaz.madrid.entities.Match;
import com.adiaz.madrid.entities.Team;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class MatchDAOImpl implements MatchDAO {
    @Override
    public Key<Match> create(Match item) throws Exception {
        return ofy().save().entity(item).now();
    }

    @Override
    public boolean update(Match item) throws Exception {
        boolean updateResult;
        if (item == null || item.getId() == null) {
            updateResult = false;
        } else {
            Match c = ofy().load().type(Match.class).id(item.getId()).now();
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
        ofy().delete().type(Match.class).id(id);
    }

    @Override
    public Match findById(String id) {
        return ofy().load().type(Match.class).id(id).now();
    }

    @Override
    public List<Match> findAll() {
        return ofy().load().type(Match.class).list();
    }

    @Override
    public int recordsCount() {
        int count = 0;
        QueryKeys<Match> matchesList = ofy().load().type(Match.class).limit(5000).keys();
        while (matchesList.list().size()==5000) {
            count += matchesList.list().size();
            matchesList = ofy().load().type(Match.class).offset(count).limit(5000).keys();
        }
        count += matchesList.list().size();
        return count;
    }

    @Override
    public void insertList(Collection<Match> matchList) {
        ofy().save().entities(matchList);
    }

    @Override
    public List<Match> findGroupsOfATeam(Long idTeam) {
        Query<Match> query = ofy().load().type(Match.class).project("idGroup").distinct(true);
        List<Match> matchesAsLocal = query.filter("idTeamLocal", idTeam).list();
        List<Match> matchesAsVisitor = query.filter("idTeamVisitor", idTeam).list();
        matchesAsLocal.addAll(matchesAsVisitor);
        return matchesAsLocal;
    }

    @Override
    public List<Match> findByCompeticion(String idCompeticion) {
        Query<Match> query = ofy().load().type(Match.class)
                .filter("idGroup", idCompeticion)
                .order("numWeek")
                .order("numMatch");
        return query.list();
    }

    @Override
    public List<Match> findMatchesPagination(int page) {
        int from = page * DeportesMadridConstants.PAGINATION_RECORDS;
        return ofy().load().type(Match.class).limit(DeportesMadridConstants.PAGINATION_RECORDS).offset(from).list();
    }
}
