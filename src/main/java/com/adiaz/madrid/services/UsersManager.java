package com.adiaz.madrid.services;

import com.adiaz.madrid.entities.User;

import java.util.List;

public interface UsersManager {
    List<User> queryAllUsers();
    User queryUserByName(String userName);
    void addUser(User user) throws Exception;
    void removeUser(String userName) throws Exception;
    boolean updateUser(User user) throws Exception;
    void removeAll() throws Exception;
}
