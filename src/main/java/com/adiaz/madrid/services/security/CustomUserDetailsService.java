package com.adiaz.madrid.services.security;

import com.adiaz.madrid.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        /** should find user by "username". */
        User user = new User();
        user.setUsername("adiaz");
        user.setEnabled(true);
        user.setBannedUser(false);
        user.setAccountNonExpired(true);
        /** password: admin */
        user.setPassword("8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918");
        user.setAdmin(true);
        return user;
    }
}
