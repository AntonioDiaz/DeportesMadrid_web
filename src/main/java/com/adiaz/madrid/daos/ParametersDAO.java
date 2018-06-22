package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.Parameter;

import java.util.List;

public interface ParametersDAO extends GenericDAO<Parameter, Long> {

    List<Parameter> findAll();
    Parameter findById(Long id);
    List<Parameter> findByKey(String key);
}
