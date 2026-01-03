package com.sales.config;

import com.sales.admin.repositories.GroupPermissionRepository;
import com.sales.admin.repositories.StorePermissionsRepository;
import com.sales.admin.repositories.UserRepository;
import com.sales.entities.SalesUser;
import com.sales.entities.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SalesAuthenticationManager implements AuthenticationManager {

    private final UserRepository userRepository;
    private final GroupPermissionRepository groupPermissionRepository;
    private final StorePermissionsRepository storePermissionsRepository;
    private  final Logger logger = LoggerFactory.getLogger(SalesAuthenticationManager.class);


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = (String) authentication.getCredentials();
        User user = userRepository.findByEmailAndPassword(email,password).orElseThrow(() -> new UsernameNotFoundException("Invalid Credentials !"));
        SalesUser userDetails = new SalesUser(user);
        //TODO : make sure password encrypt here..
        return new UsernamePasswordAuthenticationToken(
                userDetails,password,null
        );
    }
}
