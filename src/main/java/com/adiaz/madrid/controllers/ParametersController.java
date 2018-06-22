package com.adiaz.madrid.controllers;


import com.adiaz.madrid.entities.Parameter;
import com.adiaz.madrid.forms.validators.ParameterFormValidator;
import com.adiaz.madrid.services.ParametersManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/parameters")
public class ParametersController {

    @Autowired
    ParametersManager parametersManager;

    @Autowired
    ParameterFormValidator parameterFormValidator;

    @RequestMapping("/list")
    public ModelAndView list(){
        List<Parameter> parameters = parametersManager.queryParameters();
        ModelAndView modelAndView = new ModelAndView("parameters_list");
        modelAndView.addObject("parameters", parameters);
        return modelAndView;
    }

    @RequestMapping("/add")
    public ModelAndView add(){
        ModelAndView modelAndView = new ModelAndView("parameters_add");
        modelAndView.addObject("my_form", new Parameter());
        return modelAndView;
    }

    @RequestMapping("/doAdd")
    public ModelAndView doAdd(@ModelAttribute("my_form") Parameter parameter, BindingResult bindingResult) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        parameterFormValidator.validate(parameter, bindingResult);
        if (!bindingResult.hasErrors()) {
            parametersManager.add(parameter);
            modelAndView.setViewName("redirect:/parameters/list");
        } else {
            modelAndView.addObject("my_form", parameter);
            modelAndView.setViewName("parameters_add");
        }
        return modelAndView;
    }

    @RequestMapping("/doDelete")
    public String doDelete(@RequestParam(value = "id") Long id) throws Exception {
        Parameter parameter = parametersManager.queryById(id);
        parametersManager.delete(parameter);
        return "redirect:/parameters/list";
    }

    @RequestMapping("/update")
    public ModelAndView update(@RequestParam(value = "id") Long id) throws Exception {
        Parameter parameter = parametersManager.queryById(id);
        ModelAndView modelAndView = new ModelAndView("parameters_update");
        modelAndView.addObject("my_form", parameter);
        return modelAndView;
    }

    @RequestMapping("/doUpdate")
    public ModelAndView doUpdate(@ModelAttribute("my_form") Parameter parameter, BindingResult bindingResult) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        parameterFormValidator.validate(parameter, bindingResult);
        if (!bindingResult.hasErrors()) {
            parametersManager.update(parameter);
            modelAndView.setViewName("redirect:/parameters/list");
        } else {
            modelAndView.addObject("my_form", parameter);
            modelAndView.setViewName("parameters_update");
        }
        return modelAndView;
    }
}
