package com.adiaz.madrid.controllers;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home(ModelMap modelMap) {
        ModelAndView modelAndView = new ModelAndView("home");
        return modelAndView;
    }

    @RequestMapping (value="/login", method=RequestMethod.GET)
    public String goLogin(){
        return "login";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logout(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "login";
    }

    @RequestMapping (value="/loginfailed", method=RequestMethod.GET)
    public String goLoginFailed(ModelMap modelMap, HttpServletRequest request){
        Exception exception = (Exception) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = "Usuario o contraseña incorrectos";
        } else if (exception instanceof LockedException) {
            error = "Usuario bloquedao";
        } else {
            error = "Usuario o contraseña incorrectos";
        }
        modelMap.addAttribute("error", error);
        return "login";
    }
}
