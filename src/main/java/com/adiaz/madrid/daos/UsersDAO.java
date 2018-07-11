package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.User;
import com.googlecode.objectify.Key;

import java.util.List;

public interface UsersDAO extends GenericDAO<User, String> {
    Key<User> create(User user) throws Exception;
    List<User> findAll();
    User findUser(String userName);
}
