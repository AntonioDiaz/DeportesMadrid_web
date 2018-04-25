package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.ClassificationEntry;

import java.util.List;

public interface ClassificationManager {

    int classificationCount();

    List<ClassificationEntry> findClassificationByIdGroup(String idCompeticion);

}
