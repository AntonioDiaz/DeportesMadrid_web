package com.adiaz.madrid.services.security;

import com.adiaz.madrid.entities.User;
import com.adiaz.madrid.services.UsersManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    Logger logger = Logger.getLogger(CustomUserDetailsService.class);

    @Autowired
    UsersManager usersManager;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /** should find user by "username". */
        logger.debug("username: " + username);
        User user = usersManager.queryUserByName(username);
        if (user==null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
