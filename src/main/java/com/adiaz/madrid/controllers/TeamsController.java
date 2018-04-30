package com.adiaz.madrid.controllers;


import com.adiaz.madrid.entities.Team;
import com.adiaz.madrid.services.TeamManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping ("/teams")
public class TeamsController {

    private static final Logger logger = Logger.getLogger(TeamsController.class);

    @Autowired
    TeamManager teamManager;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list() {
        ModelAndView modelAndView = new ModelAndView("teams_list");
        modelAndView.addObject("teams_number", teamManager.teamsCount());
        return modelAndView;
    }
}