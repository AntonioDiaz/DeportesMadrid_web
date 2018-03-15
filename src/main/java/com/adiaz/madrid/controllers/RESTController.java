package com.adiaz.madrid.controllers;

import com.adiaz.madrid.entities.Competition;
import com.adiaz.madrid.services.CompetitionsManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("server")
public class RESTController {

    @Autowired
    CompetitionsManager competitionsManager;

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

}