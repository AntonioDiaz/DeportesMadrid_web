package com.adiaz.madrid.daos;


import com.adiaz.madrid.entities.User;
import com.googlecode.objectify.Key;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class UsersDAOImpl implements UsersDAO {

    @Override
    public Key<User> create(User item) throws Exception {
        return ofy().save().entity(item).now();
    }

    @Override
    public boolean update(User user) throws Exception {
        boolean updateResult = false;
        if (user != null && user.getUsername() != null) {
            User c = ofy().load().type(User.class).id(user.getUsername()).now();
            if (c != null) {
                ofy().save().entity(user).now();
                updateResult = true;
            }
        }
        return updateResult;
    }

    @Override
    public List<User> findAll() {
        return ofy().load().type(User.class).list();
    }

    @Override
    public void remove(String id) throws Exception {
        ofy().delete().type(User.class).id(id).now();
    }

    @Override
    public User findById(String id) {
        if (id!=null) {
            return ofy().load().type(User.class).id(id).now();
        }
        return null;
    }


    @Override
    public User findUser(String userName) {
        Key<User> key = Key.create(User.class, userName);
        return ofy().load().key(key).now();
    }
}
