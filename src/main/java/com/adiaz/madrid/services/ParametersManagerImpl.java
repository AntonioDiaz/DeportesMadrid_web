package com.adiaz.madrid.services;

import com.adiaz.madrid.daos.ParametersDAO;
import com.adiaz.madrid.entities.Parameter;
import com.adiaz.madrid.utils.DeportesMadridConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("parametersManager")
public class ParametersManagerImpl implements ParametersManager {

    @Autowired
    ParametersDAO parametersDAO;


    @Override
    public List<Parameter> queryParameters() {
        return parametersDAO.findAll();
    }

    @Override
    public String getParameterFcmKeyServer() {
        String fcmKeyServer = "";
        List<Parameter> parameterList = parametersDAO.findByKey(DeportesMadridConstants.PARAMETER_FCM_SERVER_KEY);
        if (parameterList.size()>0){
            fcmKeyServer = parameterList.get(0).getValue();
        }
        return fcmKeyServer;
    }

    @Override
    public Parameter queryByKey(String key) {
        List<Parameter> parameterList = parametersDAO.findByKey(key);
        return parameterList.size()==0?null:parameterList.get(0);
    }

    @Override
    public Parameter queryById(Long id) {
        return parametersDAO.findById(id);
    }

    @Override
    public void add(Parameter parameter) throws Exception {
        parametersDAO.create(parameter);
    }

    @Override
    public boolean update(Parameter parameter) throws Exception {
        return parametersDAO.update(parameter);
    }

    @Override
    public void delete(Parameter parameter) throws Exception {
        parametersDAO.remove(parameter.getId());
    }
}
