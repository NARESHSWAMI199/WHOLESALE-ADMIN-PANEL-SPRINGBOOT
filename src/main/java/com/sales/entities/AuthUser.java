package com.sales.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public interface AuthUser extends UserDetails {

     int getId();

     String getSlug();

     String getUserType();

     Integer getActivePlan();

     @Override
     Collection<? extends GrantedAuthority> getAuthorities();

     @Override
     String getPassword();

     @Override
     String getUsername();

     String getEmail();

     @Override
     boolean isEnabled();
}
