package com.adiaz.madrid.utils;

import com.adiaz.madrid.entities.ClassificationEntry;
import com.adiaz.madrid.entities.Competition;
import com.adiaz.madrid.entities.Match;
import lombok.Data;

import java.util.List;


@Data
public class CompetitionFull {
    private Competition competition;
    private List<Match> matches;
    private List<ClassificationEntry> classification;
}
