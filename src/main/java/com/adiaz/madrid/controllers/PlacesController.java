package com.adiaz.madrid.controllers;

import com.adiaz.madrid.entities.Place;
import com.adiaz.madrid.entities.Team;
import com.adiaz.madrid.services.PlacesManager;
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
@RequestMapping ("/places")
public class PlacesController {

    private static final Logger logger = Logger.getLogger(PlacesController.class);

    @Autowired
    PlacesManager placesManager;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list(){
        ModelAndView modelAndView = new ModelAndView("places_list");
        modelAndView.addObject("places_number", placesManager.placesCount());
        return modelAndView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public List<Place> search(@RequestParam(value = "place_name") String placeName) {
        return placesManager.findPlaces(placeName.toUpperCase());
    }
}
