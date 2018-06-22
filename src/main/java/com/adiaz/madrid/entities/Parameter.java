package com.adiaz.madrid.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class Parameter implements Serializable {

    @Id
    private Long id;

    @Index
    private String key;

    private String value;

}