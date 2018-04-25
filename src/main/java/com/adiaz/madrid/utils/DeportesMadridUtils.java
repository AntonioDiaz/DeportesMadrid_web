package com.adiaz.madrid.utils;

import com.adiaz.madrid.entities.Group;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeportesMadridUtils {

    public static String getLastReleasePublishedUrl(String url) throws Exception {
        HttpURLConnection con = (HttpURLConnection) (new URL(url).openConnection());
        con.setInstanceFollowRedirects(false);
        con.connect();
        return con.getHeaderField("Location");
    }

    public static String getLastReleasePublished(String url) throws Exception {
        String location = getLastReleasePublishedUrl(url);
        String fileName = null;
        if (StringUtils.isNotBlank(location)) {
            fileName = FilenameUtils.getName(location);
            if (StringUtils.isNotBlank(fileName)) {
                int underscore = fileName.lastIndexOf("_");
                int point = fileName.lastIndexOf(".");
                fileName = fileName.substring(underscore + 1, point);
            }
        }
        return fileName;
    }

    public static String normalizeName (String input) {
        String output = input.trim();
        /*
        output = output.replace((char)0XA5, (char)0XD1); //eñe mayuscula
        output = output.replace((char)0XB5, (char)0XC1); //tile A
        output = output.replace((char)0X90, (char)0XC9); //tile E
        output = output.replace((char)0XD6, (char)0XCD); //tile I
        output = output.replace((char)0XE0, (char)0XD3); //tile O
        output = output.replace((char)0XE9, (char)0XDA); //tile U
        output = output.replace((char)0X161, (char)0XDC); // Ü
        output = output.replace((char)0XEF, (char)0X27); // apostrofe
        output = output.replace((char)0XA6, (char)0XAA); // ª
        output = output.replace((char)0XA7, (char)0XBA); // º
        output = output.replace((char)0X20AC, (char)0XE7); // ç
        */
        return output;
    }

    public static String generateIdGroup(Group c) throws Exception{
        return generateIdGroup(c.getCodTemporada(), c.getCodCompeticion(), c.getCodFase(), c.getCodGrupo());
    }

    public static String generateIdGroup(Integer codTemporada, String codCompeticion, Integer codFase, Integer codGrupo)
            throws Exception {
        if(codTemporada!=null && StringUtils.isNotBlank(codCompeticion) && codFase!=null && codGrupo!=null) {
            return codTemporada + "|" + codCompeticion + "|" + codFase + "|" + codGrupo;
        }
        throw new Exception("Not possible to create id");
    }

    public static String generateIdMatch(Integer codTemporada, String codCompeticion, Integer codFase,
                                         Integer codGrupo, Integer weekNumber, Integer matchNumber)
            throws Exception {
        if(codTemporada!=null && StringUtils.isNotBlank(codCompeticion) && codFase!=null && codGrupo!=null && weekNumber!=null && matchNumber!=null) {
            return codTemporada + "|" + codCompeticion + "|" + codFase + "|" + codGrupo + "|" + weekNumber + "|" + matchNumber;
        }
        throw new Exception("Not possible to create id");
    }

    public static String generateIdClassification(Integer codTemporada, String codCompeticion, Integer codFase,
                                                  Integer codGrupo, Long codEquipo) throws Exception {
        if(codTemporada!=null && StringUtils.isNotBlank(codCompeticion) && codFase!=null && codGrupo!=null && codEquipo!=null) {
            return codTemporada + "|" + codCompeticion + "|" + codFase + "|" + codGrupo + "|" + codEquipo;
        }
        throw new Exception("Not possible to create id");
    }

    /**
     * 2017-10-08 14:0
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String dateStr) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.parse(dateStr);
    }

    public static String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(date);
    }
}
