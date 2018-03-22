package com.adiaz.madrid.entities;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode
public class ReleaseClassification {
    @Id
    String id;
    String publishUrl;
    Boolean updatedBucket;
    Boolean updatedClassification;
    Integer lines;
    Integer linesClassification;
}
