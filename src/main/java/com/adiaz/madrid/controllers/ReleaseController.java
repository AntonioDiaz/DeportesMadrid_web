package com.adiaz.madrid.controllers;

import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.services.ReleaseManager;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;


@Controller
@RequestMapping("/releases")
public class ReleaseController {

    public static final Logger logger = Logger.getLogger(ReleaseController.class);
    public static final String ERROR = "error";
    public static final String DONE = "done";

    @Autowired
    ReleaseManager releaseManager;

    @RequestMapping(value = "/release_list", method = RequestMethod.GET)
    public ModelAndView releaseList(@RequestParam(value = "delete_done", defaultValue = "false") boolean deleteDone) {
        ModelAndView modelAndView = new ModelAndView("release_list");
        modelAndView.addObject("releaseList", releaseManager.queryAllRelease());
        modelAndView.addObject("delete_done", deleteDone);
        return modelAndView;
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ModelAndView check() throws Exception {
        ModelAndView modelAndView = new ModelAndView("release_check");
        modelAndView.addObject("last_release", releaseManager.queryLastRelease());
        return modelAndView;
    }

    @RequestMapping(value = "/check_release_ajax", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String checkReleaseAction() throws Exception {
        try {
            Release release = releaseManager.queryLastRelease();
            if (release==null || releaseManager.publishedUpdates(release)) {
                releaseManager.createRelease();
                return "Se creo la release.";
            } else {
                return "Nada que actualizar";
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
    }

    @RequestMapping(value = "/enqueueTask", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTask() throws Exception {
        try {
            releaseManager.enqueTaskAll();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/createReleaseTask", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String createReleaseTask() throws Exception {
        logger.debug("started task " + new Date());
        try {
            //search if release existis before.
            releaseManager.updateDataStore();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        logger.debug("ended task");
        return "redirect:/";
    }

    @RequestMapping(value = "/delete_release", method = RequestMethod.GET)
    public String deleteRelease(@RequestParam(value = "id_release") String id) throws Exception {
        releaseManager.removeRelease(id);
        return "redirect:/releases/release_list?delete_done=true";
    }

    @RequestMapping(value = "/enqueueTaskTeams", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskTeams() throws Exception {
        try {
            releaseManager.enqueTaskTeams();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/enqueueTaskPlaces", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskPlaces() throws Exception {
        try {
            releaseManager.enqueTaskPlaces();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/enqueueTaskGroups", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskGroups() throws Exception {
        try {
            releaseManager.enqueTaskGroups();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/enqueueTaskMatches", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskMatches() throws Exception {
        try {
            releaseManager.enqueTaskMatches();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/enqueueTaskClassification", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskClassification() throws Exception {
        try {
            releaseManager.enqueTaskClassification();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }


    @RequestMapping(value = "/enqueueTaskEntities", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskEntities() throws Exception {
        try {
            releaseManager.enqueTaskEntities();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

}
