package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.ClassificationDAO;
import com.adiaz.madrid.daos.CompetitionDAO;
import com.adiaz.madrid.daos.TeamDAO;
import com.adiaz.madrid.entities.ClassificationEntry;
import com.adiaz.madrid.entities.Competition;
import com.adiaz.madrid.entities.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ClassificationManager")
public class ClassificationManagerImpl implements ClassificationManager {


    @Autowired
    ClassificationDAO classificationDAO;

    @Autowired
    CompetitionDAO competitionDAO;

    @Autowired
    TeamDAO teamDAO;

    @Override
    public int classificationCount() {
        return classificationDAO.recordsCount();
    }

    @Override
    public List<ClassificationEntry> findClassificationByCompetition(String idCompeticion) {
        List<ClassificationEntry> classificationEntryList = classificationDAO.findByCompeticion(idCompeticion);
        Competition competition = competitionDAO.findById(idCompeticion);
        for (ClassificationEntry classificationEntry : classificationEntryList) {
            classificationEntry.setCompetition(competition);
            classificationEntry.setTeam(teamDAO.findById(classificationEntry.getIdTeam()));
        }
        return classificationEntryList;
    }
}
