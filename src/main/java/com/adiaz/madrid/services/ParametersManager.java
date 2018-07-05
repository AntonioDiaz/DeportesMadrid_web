package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.Parameter;

import java.util.List;

public interface ParametersManager {
    List<Parameter> queryParameters();
    Parameter queryById(Long id);
    String getParameterFcmKeyServer();
    Parameter queryByKey(String key);
    void add (Parameter parameter) throws Exception;
    boolean update(Parameter parameter) throws Exception;
    void delete(Parameter parameter) throws Exception;

}
