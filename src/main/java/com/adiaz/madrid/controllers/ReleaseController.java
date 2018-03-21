package com.adiaz.madrid.controllers;

import com.adiaz.madrid.entities.ReleaseClassification;
import com.adiaz.madrid.entities.ReleaseMatches;
import com.adiaz.madrid.services.ReleaseManager;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import com.adiaz.madrid.utils.DeportesMadridUtils;
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

    @RequestMapping(value = "/release_list_matches", method = RequestMethod.GET)
    public ModelAndView releaseListMatches(@RequestParam(value = "delete_done", defaultValue = "false") boolean deleteDone) {
        ModelAndView modelAndView = new ModelAndView("release_list_matches");
        modelAndView.addObject("releaseList", releaseManager.queryAllReleaseMatches());
        modelAndView.addObject("delete_done", deleteDone);
        return modelAndView;
    }

    @RequestMapping(value = "/release_list_classifications", method = RequestMethod.GET)
    public ModelAndView releaseListClassification(@RequestParam(value = "delete_done", defaultValue = "false") boolean deleteDone) {
        ModelAndView modelAndView = new ModelAndView("release_list_classifications");
        modelAndView.addObject("releaseList", releaseManager.queryAllReleaseClassifications());
        modelAndView.addObject("delete_done", deleteDone);
        return modelAndView;
    }


    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ModelAndView check() throws Exception {
        ModelAndView modelAndView = new ModelAndView("release_check");
        String releaseAvailableMatches = DeportesMadridUtils.getLastReleasePublished(DeportesMadridConstants.URL_MATCHES);
        String releaseAvailableClassifications = DeportesMadridUtils.getLastReleasePublished(DeportesMadridConstants.URL_CLASSIFICATION);
        modelAndView.addObject("last_release_available_matches", releaseAvailableMatches);
        modelAndView.addObject("last_release_available_classifications", releaseAvailableClassifications);
        ReleaseMatches lastReleaseMatches = releaseManager.queryReleaseMatches(releaseAvailableMatches);
        ReleaseClassification releaseClassification = releaseManager.queryReleaseClassifications(releaseAvailableClassifications);
        modelAndView.addObject("lastReleaseMatches", lastReleaseMatches);
        modelAndView.addObject("lastReleaseClassifications", releaseClassification);
        return modelAndView;
    }

    @RequestMapping(value = "/enqueueTask", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTask() throws Exception {
        try {
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(TaskOptions.Builder.withUrl("/releases/createReleaseTask"));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/createReleaseMatches", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String createReleaseMatchescreateRelease() throws Exception {
        try {
            releaseManager.createOrGetLastReleasePublishedMatches();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/createReleaseClassification", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String createReleaseClassification() throws Exception {
        try {
            releaseManager.createOrGetLastReleasePublishedClassification();
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

    @RequestMapping(value = "/loadBucketMatches", method = RequestMethod.GET)
    @ResponseBody
    public String loadBucketMatches(@RequestParam(value = "id_release") String idRelease) throws Exception {
        try {
            releaseManager.loadBucketMatches(idRelease);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/loadBucketClassification", method = RequestMethod.GET)
    @ResponseBody
    public String loadBucketClassification(@RequestParam(value = "id_release") String idRelease) throws Exception {
        try {
            releaseManager.loadBucketClassification(idRelease);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/loadTeams", method = RequestMethod.GET)
    @ResponseBody
    public String loadTeams(@RequestParam(value = "id_release") String idRelease) throws Exception {
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
    public String loadPlaces(@RequestParam(value = "id_release") String idRelease) throws Exception {
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
    public String loadCompetitions(@RequestParam(value = "id_release") String idRelease) throws Exception {
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
    public String loadMatches(@RequestParam(value = "id_release") String idRelease) throws Exception {
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
    public String loadClassifications(@RequestParam(value = "id_release") String idRelease) throws Exception {
        try {
            releaseManager.updateClassifications(idRelease);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return DONE;
    }

    @RequestMapping(value = "/delete_release_matches", method = RequestMethod.GET)
    public String deleteReleaseMatches(@RequestParam(value = "id_release") String id) throws Exception {
        releaseManager.removeReleaseMatches(id);
        return "redirect:/releases/release_list_matches?delete_done=true";
    }

    @RequestMapping(value = "/delete_release_classifications", method = RequestMethod.GET)
    public String deleteReleaseClassifications(@RequestParam(value = "id_release") String id) throws Exception {
        releaseManager.removeReleaseClassification(id);
        return "redirect:/releases/release_list_classifications?delete_done=true";
    }
}
