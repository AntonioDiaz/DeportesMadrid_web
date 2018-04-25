package com.adiaz.madrid.controllers;


import com.adiaz.madrid.entities.Group;
import com.adiaz.madrid.services.GroupManager;
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
@RequestMapping ("/groups")
public class GroupsController {

    @Autowired
    GroupManager groupManager;

    private static final Logger logger = Logger.getLogger(GroupsController.class);

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list(){
        ModelAndView modelAndView = new ModelAndView("groups_list");
        modelAndView.addObject("groups_number", groupManager.countGroups());
        return modelAndView;
    }

    @RequestMapping(value = "/sports_list", method = RequestMethod.GET)
    public ModelAndView sportsList(){
        ModelAndView modelAndView = new ModelAndView("groups_sports_list");
        modelAndView.addObject("sports_list_count", groupManager.distinctSportsCount());
        return modelAndView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public List<Group> search(@RequestParam(value = "group_name") String groupName) {
        return groupManager.findGroups(groupName.toUpperCase());
    }
}
