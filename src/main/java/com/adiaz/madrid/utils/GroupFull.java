package com.adiaz.madrid.utils;

import com.adiaz.madrid.entities.ClassificationEntry;
import com.adiaz.madrid.entities.Group;
import com.adiaz.madrid.entities.Match;
import lombok.Data;

import java.util.List;


@Data
public class GroupFull {
    private Group group;
    private List<Match> matches;
    private List<ClassificationEntry> classification;
}
