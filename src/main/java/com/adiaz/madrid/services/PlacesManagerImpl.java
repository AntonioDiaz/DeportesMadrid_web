package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.PlaceDAO;
import com.adiaz.madrid.entities.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("PlacesManager")
public class PlacesManagerImpl implements PlacesManager {

    @Autowired
    PlaceDAO placeDAO;


    @Override
    public int placesCount() {
        return placeDAO.findAll().size();
    }

    @Override
    public List<Place> findPlaces(String placeName) {
        List<Place> placesFound = new ArrayList<>();
        List<Place> places = placeDAO.findAll();
        for (Place place : places) {
            if (place.getName().contains(placeName)) {
                placesFound.add(place);
            }
        }
        return placesFound;
    }
}
