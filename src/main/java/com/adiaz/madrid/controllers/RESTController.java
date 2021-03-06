package com.adiaz.madrid.controllers;

import com.adiaz.madrid.entities.ClassificationEntry;
import com.adiaz.madrid.entities.Group;
import com.adiaz.madrid.entities.Match;
import com.adiaz.madrid.entities.Team;
import com.adiaz.madrid.services.ClassificationManager;
import com.adiaz.madrid.services.GroupManager;
import com.adiaz.madrid.services.MatchesManager;
import com.adiaz.madrid.services.TeamManager;
import com.adiaz.madrid.utils.GroupFull;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("server")
public class RESTController {

    @Autowired
    GroupManager groupManager;

    @Autowired
    MatchesManager matchesManager;

    @Autowired
    ClassificationManager classificationManager;

    @Autowired
    TeamManager teamManager;

    private static final Logger logger = Logger.getLogger(RESTController.class);

    @RequestMapping(value = "/sports", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getSports() {
       return groupManager.distinctSports();
    }

    @RequestMapping(value = "/distritos/{sport}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getDistritos(@PathVariable String sport) {
        return groupManager.distinctDistritos(sport);
    }

    @RequestMapping(value = "/grupos/{sport}/{distrito}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getGrupos(@PathVariable String sport, @PathVariable String distrito) {
        return groupManager.distinctDistritos(sport);
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Group> groups() {
        return groupManager.findAllGroups();
    }

    @RequestMapping(value = "/groups/{codTemporada}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Group> groups(@PathVariable(value="codTemporada") Integer codTemporada) {
        return groupManager.findGroups(codTemporada);
    }

    @RequestMapping(value = "/findMatches", method = RequestMethod.GET)
    @ResponseBody
    public List<Match> findMatches(@RequestParam(value = "cod_competicion") String idGroup) {
        List<Match> matchesByIdGroup = matchesManager.findMatchesByIdGroup(idGroup);
        return matchesByIdGroup;
    }

    @RequestMapping(value = "/findClassification", method = RequestMethod.GET)
    @ResponseBody
    public List<ClassificationEntry> findClassification(@RequestParam(value = "cod_group") String idGroup) {
        List<ClassificationEntry> classificationEntries = classificationManager.findClassificationByIdGroup(idGroup);
        return classificationEntries;
    }

    @RequestMapping(value = "/findGroup", method = RequestMethod.GET)
    @ResponseBody
    public GroupFull findMatchesAndGroup(@RequestParam(value = "cod_group") String codGroup) {
        Group group = groupManager.findById(codGroup);
        List<Match> matches = matchesManager.findMatchesByIdGroup(codGroup);
        List<ClassificationEntry> classificationEntries = classificationManager.findClassificationByIdGroup(codGroup);
        GroupFull groupFull = new GroupFull();
        groupFull.setGroup(group);
        groupFull.setMatches(matches);
        groupFull.setClassification(classificationEntries);
        return groupFull;
    }

    @RequestMapping(value = "/findTeamAll", method = RequestMethod.GET)
    @ResponseBody
    public List<Team> findTeamAll() {
        return teamManager.findAll();
    }

    @RequestMapping(value = "/findTeam", method = RequestMethod.GET)
    @ResponseBody
    public List<Team> findTeam(
            @RequestParam(value = "team_name") String teamName) {
        return teamManager.findTeams(teamName.toUpperCase());
    }
}