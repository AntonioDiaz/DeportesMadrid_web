package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.ReleaseMatches;
import com.googlecode.objectify.Key;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;


@Repository
public class ReleaseMatchesDAOImpl implements ReleaseMatchesDAO {

    @Override
    public Key<ReleaseMatches> create(ReleaseMatches item) throws Exception {
        return ofy().save().entity(item).now();
    }

    @Override
    public boolean update(ReleaseMatches item) throws Exception {
        boolean updateResult;
        if (item == null || item.getId() == null) {
            updateResult = false;
        } else {
            ReleaseMatches c = ofy().load().type(ReleaseMatches.class).id(item.getId()).now();
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
        ofy().delete().type(ReleaseMatches.class).id(id).now();
    }

    @Override
    public ReleaseMatches findById(String id) {
        return ofy().load().type(ReleaseMatches.class).id(id).now();
    }

    @Override
    public List<ReleaseMatches> findAll() {
        return ofy().load().type(ReleaseMatches.class).orderKey(true).list();
    }


}
