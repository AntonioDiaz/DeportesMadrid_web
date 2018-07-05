package com.adiaz.madrid.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.Data;

@Entity
@Data
public class Notification {
    @Id
    private Long id;

    @Index
    private String title;

    private String body;

}
