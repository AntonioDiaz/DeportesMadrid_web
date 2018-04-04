package com.adiaz.madrid.controllers;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/catalogo")
public class FakeServer {

    private static final Logger logger = Logger.getLogger(FakeServer.class);

    @RequestMapping(value = "/matches.csv", method = RequestMethod.GET)
    public String matches() {
        return "redirect:/catalogo/Partidos_20180403.csv";
    }

    @RequestMapping(value = "/classification.csv", method = RequestMethod.GET)
    public String classification() {
        return "redirect:/catalogo/clasificaciones_20180403.csv";
    }

    @RequestMapping(value = "/{file_name:.+}", method = RequestMethod.GET)
    public void getFile(@PathVariable("file_name") String fileName, HttpServletResponse response) {
        try {
            // get your file as InputStream
            File initialFile = new File(fileName);
            InputStream is = getClass().getResourceAsStream("/" + fileName);
            // copy it to response's OutputStream
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            logger.error("Error writing file to output stream. Filename was" + fileName, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }

    }

}
