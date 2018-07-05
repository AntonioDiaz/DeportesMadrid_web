package com.adiaz.madrid.controllers;

import com.adiaz.madrid.entities.Notification;
import com.adiaz.madrid.services.ParametersManager;
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

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/notifications")
public class NotificationsController {
    public static final Logger logger = Logger.getLogger(NotificationsController.class);

    @Autowired
    ParametersManager parametersManager;

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public ModelAndView send() {
        ModelAndView modelAndView = new ModelAndView("notification_send");
        modelAndView.addObject("my_form", new Notification());
        return modelAndView;
    }

    @RequestMapping(value = "/doSend", method={RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String doSend(@RequestParam(value = "title") String title, @RequestParam(value = "body") String body)
            throws Exception {
        try {
            String fcmKeyServer = parametersManager.queryByKey(DeportesMadridConstants.PARAMETER_FCM_SERVER_KEY).getValue();
            long code = DeportesMadridUtils.sendNotificationInfo(fcmKeyServer, title, body);
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
