package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.ClassificationDAO;
import com.adiaz.madrid.entities.ClassificationEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ClassificationManager")
public class ClassificationManagerImpl implements ClassificationManager {


    @Autowired
    ClassificationDAO classificationDAO;

    @Override
    public int classificationCount() {
        return classificationDAO.recordsCount();
    }

    @Override
    public List<ClassificationEntry> findClassificationByCompetition(String idCompeticion) {
        return classificationDAO.findByCompeticion(idCompeticion);
    }
}
