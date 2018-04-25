package com.adiaz.madrid.controllers;


import com.adiaz.madrid.entities.Group;
import com.adiaz.madrid.services.ClassificationManager;
import com.adiaz.madrid.services.GroupManager;
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
    GroupManager groupManager;

    @Autowired
    ClassificationManager classificationManager;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list(){
        ModelAndView modelAndView = new ModelAndView("matches_list");
        modelAndView.addObject("temporadas", groupManager.distinctTemporadas());
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

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    @ResponseBody
    public List<Group> groups(@RequestParam(value = "cod_temporada") Integer codTemporada) {
        return groupManager.distinctGroups(codTemporada);
    }

    @RequestMapping(value = "/fases", method = RequestMethod.GET)
    @ResponseBody
    public List<Group> fases(@RequestParam(value = "cod_temporada") Integer codTemporada,
                             @RequestParam(value = "cod_competicion") String codCompeticion) {
        return groupManager.distinctGroups(codTemporada, codCompeticion);
    }

    @RequestMapping(value = "/grupos", method = RequestMethod.GET)
    @ResponseBody
    public List<Group> grupos(@RequestParam(value = "cod_temporada") Integer codTemporada,
                              @RequestParam(value = "cod_competicion") String codCompeticion,
                              @RequestParam(value = "cod_fase") Integer codFase) {
        return groupManager.distinctGroups(codTemporada, codCompeticion, codFase);
    }
}
