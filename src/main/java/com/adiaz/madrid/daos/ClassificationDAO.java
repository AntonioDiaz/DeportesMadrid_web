package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.ClassificationEntry;

import java.util.Collection;
import java.util.List;

public interface ClassificationDAO extends GenericDAO<ClassificationEntry, String> {
    int recordsCount();
    List<ClassificationEntry> findByCompeticion(String idCompeticion);

    void insertList(Collection<ClassificationEntry> values);
}
