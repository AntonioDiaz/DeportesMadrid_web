package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Place;
import com.adiaz.madrid.entities.Team;
import com.googlecode.objectify.Key;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class PlaceDAOImpl implements PlaceDAO {

    @Override
    public Key<Place> create(Place item) throws Exception {
        return ofy().save().entity(item).now();
    }

    @Override
    public boolean update(Place item) throws Exception {
        boolean updateResult;
        if (item == null || item.getId() == null) {
            updateResult = false;
        } else {
            Place c = ofy().load().type(Place.class).id(item.getId()).now();
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
        ofy().delete().type(Place.class).id(id).now();
    }

    @Override
    public Place findById(Long id) {
        return ofy().load().type(Place.class).id(id).now();
    }

    @Override
    public List<Place> findAll() {
        return ofy().load().type(Place.class).list();
    }

    @Override
    public List<Place> findByName(String name) {
        return ofy().load().type(Place.class).filter("name >=", name).filter("name <=", name + "\uFFFD").list();
    }

    @Override
    public void insertList(Collection<Place> places) {
        ofy().save().entities(places).now();
    }

}
