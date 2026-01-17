package com.sales.claims;

import com.sales.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SalesUser implements UserDetails, AuthUser {

    private final User user;

    public SalesUser(User user){
        this.user = user;
    }

    @Override
    public int getId(){
        return user.getId();
    }

    @Override
    public String getSlug(){
        return user.getSlug();
    }

    @Override
    public String getUserType(){
        return user.getUserType();
    }

    @Override
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
