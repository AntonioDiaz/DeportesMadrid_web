package com.adiaz.madrid.utils;

import com.adiaz.madrid.daos.CompetitionDAO;
import com.adiaz.madrid.entities.*;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.QueryKeys;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class RegisterEntities {

    private static final Logger logger = Logger.getLogger(RegisterEntities.class);

    @Autowired
    CompetitionDAO competitionDAO;

    public void init() throws Exception {


        ObjectifyService.register(Release.class);
        ObjectifyService.register(User.class);
        ObjectifyService.register(Team.class);
        ObjectifyService.register(Place.class);
        ObjectifyService.register(Competition.class);
        ObjectifyService.register(Match.class);
        ObjectifyService.register(ClassificationEntry.class);

        /* clean DB. */
        try {
            List<Key<Release>> listRelease = ofy().load().type(Release.class).keys().list();
            ofy().delete().keys(listRelease);
            List<Key<Competition>> listCompetitions = ofy().load().type(Competition.class).keys().list();
            ofy().delete().keys(listCompetitions);
            List<Key<Team>> listTeam = ofy().load().type(Team.class).keys().list();
            ofy().delete().keys(listTeam);
            List<Key<Place>> placesList = ofy().load().type(Place.class).keys().list();
            ofy().delete().keys(placesList);
            QueryKeys<Match> matchesList = ofy().load().type(Match.class).limit(5000).keys();
            while (matchesList.list().size() != 0) {
                ofy().delete().keys(matchesList).now();
                logger.debug("delete redords: " + matchesList.list().size());
                matchesList = ofy().load().type(Match.class).limit(5000).keys();
            }
            matchesList = ofy().load().type(Match.class).keys();
            ofy().delete().keys(matchesList).now();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            ofy().clear();
        }
        logger.debug("init DataBase finished");
    }
}
