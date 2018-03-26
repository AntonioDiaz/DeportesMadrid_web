package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.Place;

import java.util.List;

public interface PlacesManager {

    int placesCount();

    List<Place> findPlaces(String placeName);
}
