package com.sales.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SalesUser implements UserDetails {

    private final User user;

    public SalesUser(User user){
        this.user = user;
    }

    public Integer getId(){
        return user.getId();
    }

    public String getSlug(){
        return user.getSlug();
    }


    public String getUserType(){
        return user.getUserType();
    }

    public Integer getActivePlan(){
        return user.getActivePlan();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().equals("A");
    }
}
