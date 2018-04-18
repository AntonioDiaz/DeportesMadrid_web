package com.adiaz.madrid.controllers;


import com.adiaz.madrid.entities.Competition;
import com.adiaz.madrid.services.CompetitionsManager;
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
@RequestMapping ("/competitions")
public class CompetitionsController {

    @Autowired
    CompetitionsManager competitionsManager;

    private static final Logger logger = Logger.getLogger(CompetitionsController.class);

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list(){
        ModelAndView modelAndView = new ModelAndView("competitions_list");
        modelAndView.addObject("competitions_number", competitionsManager.competitionsCount());
        return modelAndView;
    }

    @RequestMapping(value = "/sports_list", method = RequestMethod.GET)
    public ModelAndView sportsList(){
        ModelAndView modelAndView = new ModelAndView("competitions_sports_list");
        modelAndView.addObject("sports_list_count", competitionsManager.distinctSportsCount());
        return modelAndView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public List<Competition> search(@RequestParam(value = "competition_name") String competitionName) {
        return competitionsManager.findCompetitions(competitionName.toUpperCase());
    }
}
