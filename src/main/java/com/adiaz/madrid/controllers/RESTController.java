package com.adiaz.madrid.controllers;

import com.adiaz.madrid.entities.ClassificationEntry;
import com.adiaz.madrid.entities.Competition;
import com.adiaz.madrid.entities.Match;
import com.adiaz.madrid.services.ClassificationManager;
import com.adiaz.madrid.services.CompetitionsManager;
import com.adiaz.madrid.services.MatchesManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("server")
public class RESTController {

    @Autowired
    CompetitionsManager competitionsManager;

    @Autowired
    MatchesManager matchesManager;

    @Autowired
    ClassificationManager classificationManager;

    private static final Logger logger = Logger.getLogger(RESTController.class);

    @RequestMapping(value = "/sports", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getSports() {
       return competitionsManager.distinctSports();
    }

    @RequestMapping(value = "/distritos/{sport}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getDistritos(@PathVariable String sport) {
        return competitionsManager.distinctDistritos(sport);
    }

    @RequestMapping(value = "/grupos/{sport}/{distrito}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getGrupos(@PathVariable String sport, @PathVariable String distrito) {
        return competitionsManager.distinctDistritos(sport);
    }


    @RequestMapping(value = "/competiciones", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Competition> getCompeticiones() {
        return competitionsManager.findAllCompetitions();
    }

    @RequestMapping(value = "/findMatches", method = RequestMethod.GET)
    @ResponseBody
    public List<Match> findMatches(@RequestParam(value = "cod_competicion") String codCompeticion) {
        List<Match> matchesByCompetition = matchesManager.findMatchesByCompetition(codCompeticion);
        return matchesByCompetition;
    }

    @RequestMapping(value = "/findClassification", method = RequestMethod.GET)
    @ResponseBody
    public List<ClassificationEntry> findClassification(@RequestParam(value = "cod_competicion") String codCompeticion) {
        List<ClassificationEntry> classificationEntries = classificationManager.findClassificationByCompetition(codCompeticion);
        return classificationEntries;
    }

}