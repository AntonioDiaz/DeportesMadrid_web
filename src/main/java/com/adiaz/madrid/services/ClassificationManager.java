package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.ClassificationEntry;
import com.adiaz.madrid.entities.Match;

import java.util.List;

public interface ClassificationManager {

    int classificationCount();
    List<ClassificationEntry> findClassificationByCompetition(String idCompeticion);


}
