package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Place;

import java.util.Collection;
import java.util.List;

public interface PlaceDAO extends GenericDAO<Place> {
    List<Place> findByName(String name);
    void create(Collection<Place> places);
}
