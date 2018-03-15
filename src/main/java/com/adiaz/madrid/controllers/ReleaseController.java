package com.adiaz.madrid.controllers;

import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.services.ReleaseManager;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/release")
public class ReleaseController {

    public static final Logger logger = Logger.getLogger(ReleaseController.class);
    public static final String ERROR = "error";
    public static final String DONE = "done";

    @Autowired
    ReleaseManager releaseManager;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list(@RequestParam(value = "delete_done", defaultValue = "false") boolean deleteDone) {
        ModelAndView modelAndView = new ModelAndView("release_list");
        modelAndView.addObject("releaseList", releaseManager.queryAllRelease());
        modelAndView.addObject("delete_done", deleteDone);
        return modelAndView;
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ModelAndView check() throws Exception {
        ModelAndView modelAndView = new ModelAndView("release_check");
        String releaseAvailable = DeportesMadridUtils.getLastReleasePublished();
        modelAndView.addObject("last_release_available", releaseAvailable);
        Release lastRelease = releaseManager.lastRelease();
        if (lastRelease!=null) {
            modelAndView.addObject("last_release_loaded", lastRelease);
            modelAndView.addObject("is_last_release_loaded", lastRelease.getPublishDateStr().equals(releaseAvailable));
        }

        modelAndView.addObject("need_create", false);
        modelAndView.addObject("need_bucket", false);
        modelAndView.addObject("need_teams", false);
        modelAndView.addObject("need_places", false);
        modelAndView.addObject("need_competitions", false);
        modelAndView.addObject("need_matches", false);
        modelAndView.addObject("need_classification", false);

        //check if load is necesary.
        if (lastRelease==null || !lastRelease.getPublishDateStr().equals(releaseAvailable)) {
            modelAndView.addObject("need_create", true);
        } else if (!lastRelease.getUpdatedBucket()) {
            modelAndView.addObject("need_bucket", true);
        } else if (!lastRelease.getUpdatedTeams()) {
            modelAndView.addObject("need_teams", true);
        } else if (!lastRelease.getUpdatedPlaces()) {
            modelAndView.addObject("need_places", true);
        } else if (!lastRelease.getUpdateCompetitions()) {
            modelAndView.addObject("need_competitions", true);
        } else if (!lastRelease.getUpdatedMatches()) {
            modelAndView.addObject("need_matches", true);
        } else if (!lastRelease.getUpdatedClassification()) {
            modelAndView.addObject("need_classification", true);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/createRelease", method = RequestMethod.GET)
    @ResponseBody
    public String createRelease() throws Exception {
        try {
            releaseManager.createEmptyRelease();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/loadBucket", method = RequestMethod.GET)
    @ResponseBody
    public String loadBucket(@RequestParam(value = "id_release") Long idRelease) throws Exception {
        try {
            releaseManager.loadBucket(idRelease);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/loadTeams", method = RequestMethod.GET)
    @ResponseBody
    public String loadTeams(@RequestParam(value = "id_release") Long idRelease) throws Exception {
        try {
            releaseManager.updateTeams(idRelease);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/loadPlaces", method = RequestMethod.GET)
    @ResponseBody
    public String loadPlaces(@RequestParam(value = "id_release") Long idRelease) throws Exception {
        try {
            releaseManager.updatePlaces(idRelease);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/loadCompetitions", method = RequestMethod.GET)
    @ResponseBody
    public String loadCompetitions(@RequestParam(value = "id_release") Long idRelease) throws Exception {
        try {
            releaseManager.updateCompetitions(idRelease);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/loadMatches", method = RequestMethod.GET)
    @ResponseBody
    public String loadMatches(@RequestParam(value = "id_release") Long idRelease) throws Exception {
        try {
            releaseManager.updateMatches(idRelease);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/loadClassifications", method = RequestMethod.GET)
    @ResponseBody
    public String loadClassifications(@RequestParam(value = "id_release") Long idRelease) throws Exception {
        try {
            releaseManager.updateClassifications(idRelease);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String delete(@RequestParam(value = "id_release") Long id) throws Exception {
        releaseManager.removeRelease(id);
        return "redirect:/release/list?delete_done=true";
    }
}
