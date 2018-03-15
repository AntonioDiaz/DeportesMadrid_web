package com.adiaz.madrid.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.Data;

@Data
@Entity
public class Place {

    @Id
    private Long id;

    @Index
    private String name;
    private Long coordX;
    private Long coordY;
}
