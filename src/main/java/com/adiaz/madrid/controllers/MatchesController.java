package com.adiaz.madrid.controllers;


import com.adiaz.madrid.entities.ClassificationEntry;
import com.adiaz.madrid.entities.Competition;
import com.adiaz.madrid.entities.Match;
import com.adiaz.madrid.services.ClassificationManager;
import com.adiaz.madrid.services.CompetitionsManager;
import com.adiaz.madrid.services.MatchesManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping ("/matches")
public class MatchesController {

    private static final Logger logger = Logger.getLogger(MatchesController.class);

    @Autowired
    MatchesManager matchesManager;

    @Autowired
    CompetitionsManager competitionsManager;

    @Autowired
    ClassificationManager classificationManager;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list(){
        ModelAndView modelAndView = new ModelAndView("matches_list");
        modelAndView.addObject("temporadas", competitionsManager.distinctTemporadas());
        return modelAndView;
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ResponseBody
    public Integer count(){
        return matchesManager.matchesCount();
    }


    @RequestMapping(value = "/countClassifications", method = RequestMethod.GET)
    @ResponseBody
    public Integer countClassifications(){
        return classificationManager.classificationCount();
    }

    @RequestMapping(value = "/competitions", method = RequestMethod.GET)
    @ResponseBody
    public List<Competition> competitions(@RequestParam(value = "cod_temporada") Integer codTemporada) {
        return competitionsManager.distinctCompeticion(codTemporada);
    }

    @RequestMapping(value = "/fases", method = RequestMethod.GET)
    @ResponseBody
    public List<Competition> fases(@RequestParam(value = "cod_temporada") Integer codTemporada, @RequestParam(value = "cod_competicion") String codCompeticion) {
        return competitionsManager.distinctFase(codTemporada, codCompeticion);
    }

    @RequestMapping(value = "/grupos", method = RequestMethod.GET)
    @ResponseBody
    public List<Competition> grupos(@RequestParam(value = "cod_temporada") Integer codTemporada,
                                    @RequestParam(value = "cod_competicion") String codCompeticion,
                                    @RequestParam(value = "cod_fase") Integer codFase) {
        return competitionsManager.distinctGrupo(codTemporada, codCompeticion, codFase);
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
