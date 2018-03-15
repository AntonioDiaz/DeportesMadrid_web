package com.adiaz.madrid.services.security;


import com.adiaz.madrid.entities.User;

public interface CurrentUserManager {

    public User getEnabledUser();

}