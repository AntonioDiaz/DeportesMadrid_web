package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.ClassificationEntry;
import com.adiaz.madrid.entities.ReleaseClassification;
import com.googlecode.objectify.Key;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class ReleaseClassificationDAOImpl implements ReleaseClassificationDAO {
    @Override
    public Key<ReleaseClassification> create(ReleaseClassification item) throws Exception {
        return ofy().save().entity(item).now();
    }

    @Override
    public boolean update(ReleaseClassification item) throws Exception {
        boolean updateResult;
        if (item == null || item.getId() == null) {
            updateResult = false;
        } else {
            ReleaseClassification c = ofy().load().type(ReleaseClassification.class).id(item.getId()).now();
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
    public List<ReleaseClassification> findAll() {
        return ofy().load().type(ReleaseClassification.class).orderKey(true).list();
    }

    @Override
    public void remove(String id) throws Exception {
        ofy().delete().type(ReleaseClassification.class).id(id).now();

    }

    @Override
    public ReleaseClassification findById(String id) {
        return ofy().load().type(ReleaseClassification.class).id(id).now();
    }
}
