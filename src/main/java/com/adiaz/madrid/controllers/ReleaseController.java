package com.adiaz.madrid.controllers;

import com.adiaz.madrid.entities.Release;
import com.adiaz.madrid.services.ParametersManager;
import com.adiaz.madrid.services.ReleaseManager;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import com.adiaz.madrid.utils.DeportesMadridUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Controller
@RequestMapping("/releases")
public class ReleaseController {

    public static final Logger logger = Logger.getLogger(ReleaseController.class);

    @Autowired
    ReleaseManager releaseManager;

    @Autowired
    ParametersManager parametersManager;

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
            /*check if there is something to publish. */
            if (release==null || releaseManager.publishedUpdates(release)) {
                releaseManager.createRelease();
                return "Se creo la release.";
            } else {
                return "Nada que actualizar";
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DeportesMadridConstants.ERROR;
        }
    }

    @RequestMapping(value = "/enqueueTask", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTask(@RequestParam(value = "id") String id) throws Exception {
        try {
            Release release = releaseManager.queryReleaseById(id);
            releaseManager.enqueTaskAll(release);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DeportesMadridConstants.ERROR;
        }
        return DeportesMadridConstants.DONE;
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
    public String deleteRelease(@RequestParam(value = "id") String id) throws Exception {
        releaseManager.removeRelease(id);
        return "redirect:/releases/release_list?delete_done=true";
    }

    @RequestMapping(value = "/enqueueTaskTeams", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskTeams(@RequestParam(value = "id") String id) throws Exception {
        try {
            Release release = releaseManager.queryReleaseById(id);
            releaseManager.enqueTaskTeams(release);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DeportesMadridConstants.ERROR;
        }
        return DeportesMadridConstants.DONE;
    }

    @RequestMapping(value = "/enqueueTaskPlaces", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskPlaces(@RequestParam(value = "id") String id) throws Exception {
        try {
            Release release = releaseManager.queryReleaseById(id);
            releaseManager.enqueTaskPlaces(release);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DeportesMadridConstants.ERROR;
        }
        return DeportesMadridConstants.DONE;
    }

    @RequestMapping(value = "/enqueueTaskGroups", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskGroups(@RequestParam(value = "id") String id) throws Exception {
        try {
            Release release = releaseManager.queryReleaseById(id);
            releaseManager.enqueTaskGroups(release);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DeportesMadridConstants.ERROR;
        }
        return DeportesMadridConstants.DONE;
    }

    @RequestMapping(value = "/enqueueTaskMatches", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskMatches(@RequestParam(value = "id") String id) throws Exception {
        try {
            Release release = releaseManager.queryReleaseById(id);
            releaseManager.enqueTaskMatches(release);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DeportesMadridConstants.ERROR;
        }
        return DeportesMadridConstants.DONE;
    }

    @RequestMapping(value = "/enqueueTaskClassification", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskClassification(@RequestParam(value = "id") String id) throws Exception {
        try {
            Release release = releaseManager.queryReleaseById(id);
            releaseManager.enqueTaskClassification(release);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DeportesMadridConstants.ERROR;
        }
        return DeportesMadridConstants.DONE;
    }


    @RequestMapping(value = "/enqueueTaskEntities", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String enqueueTaskEntities(@RequestParam(value = "id") String id) throws Exception {
        try {
            Release release = releaseManager.queryReleaseById(id);
            releaseManager.enqueTaskEntities(release);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DeportesMadridConstants.ERROR;
        }
        return DeportesMadridConstants.DONE;
    }

    @RequestMapping(value = "/sendNotification", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String sendNotification() throws Exception {
        try {
            String fcmKeyServer = parametersManager.queryByKey(DeportesMadridConstants.PARAMETER_FCM_SERVER_KEY).getValue();
            Set<String[]> set = new HashSet<>();
            long code = DeportesMadridUtils.sendNotificationToFirebase(fcmKeyServer, set);
            if (code == -1) {
                return DeportesMadridConstants.ERROR;
            }
            return DeportesMadridConstants.DONE;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DeportesMadridConstants.ERROR;
        }
    }
}