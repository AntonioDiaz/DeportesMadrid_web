package com.adiaz.madrid.entities;

import com.googlecode.objectify.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode
public class Team implements Serializable {

    @Id
    private Long id;

    @Index
    private String name;

    private Set<String> groups = new HashSet<>();

}