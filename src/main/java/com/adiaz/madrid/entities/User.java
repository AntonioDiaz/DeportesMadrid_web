package com.adiaz.madrid.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
public class User implements UserDetails {

    @Id
    private String username;

    private String password;

    @Ignore
    private String password01;

    @Ignore
    private String password02;

    @Ignore
    private boolean updatePassword;

    private boolean admin;

    private boolean enabled;

    private boolean bannedUser;

    private boolean accountNonExpired;

    public User() { }

    public User(String username, String password, boolean admin, boolean enabled, boolean bannedUser, boolean accountNonExpired) {
        this.username = username;
        this.password = password;
        this.admin = admin;
        this.enabled = enabled;
        this.bannedUser = bannedUser;
        this.accountNonExpired = accountNonExpired;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (admin) {
            authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return authorityList;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !bannedUser;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return accountNonExpired;
    }
}
