package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Release;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class ReleaseDAOImpl implements ReleaseDAO {
    @Override
    public Key<Release> create(Release item) throws Exception {
        return ofy().save().entity(item).now();
    }

    @Override
    public boolean update(Release item) throws Exception {
        boolean updateResult;
        if (item == null || item.getId() == null) {
            updateResult = false;
        } else {
            Release c = ofy().load().type(Release.class)
                    .id(item.getId()).now();
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
    public List<Release> findAll() {
        return ofy().load().type(Release.class).orderKey(true).list();
    }

    @Override
    public void remove(String id) throws Exception {
        ofy().delete().type(Release.class).id(id).now();
    }

    @Override
    public Release findById(String id) {
        return ofy().load().type(Release.class).id(id).now();
    }

}
