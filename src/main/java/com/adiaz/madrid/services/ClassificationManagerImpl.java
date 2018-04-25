package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.ClassificationDAO;
import com.adiaz.madrid.daos.GroupDAO;
import com.adiaz.madrid.daos.TeamDAO;
import com.adiaz.madrid.entities.ClassificationEntry;
import com.adiaz.madrid.entities.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ClassificationManager")
public class ClassificationManagerImpl implements ClassificationManager {


    @Autowired
    ClassificationDAO classificationDAO;

    @Autowired
    GroupDAO groupDAO;

    @Autowired
    TeamDAO teamDAO;

    @Override
    public int classificationCount() {
        return classificationDAO.recordsCount();
    }

    @Override
    public List<ClassificationEntry> findClassificationByIdGroup(String idCompeticion) {
        List<ClassificationEntry> classificationEntryList = classificationDAO.findByCompeticion(idCompeticion);
        Group competition = groupDAO.findById(idCompeticion);
        for (ClassificationEntry classificationEntry : classificationEntryList) {
            classificationEntry.setGroup(competition);
            classificationEntry.setTeam(teamDAO.findById(classificationEntry.getIdTeam()));
        }
        return classificationEntryList;
    }
}
