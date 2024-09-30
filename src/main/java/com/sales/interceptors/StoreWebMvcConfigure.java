package com.sales.interceptors;

import com.sales.admin.repositories.PermissionRepository;
import com.sales.admin.repositories.UserRepository;
import com.sales.jwtUtils.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;


@Configuration
@EnableWebMvc
public class StoreWebMvcConfigure implements WebMvcConfigurer {

    @Autowired
    JwtToken jwtToken;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String arr[] = {
                "/admin/auth/login",
                "/wholesale/auth/login",
                "/wholesale/auth/register",
                "/v3/api-docs",
                "/swagger-resources/**",
                "/swagger-ui/**",
                "/webjars/**",
                "/admin/auth/profile/**",
                "/admin/store/image/**",
                "/admin/item/image/**"

        };
        registry.addInterceptor(new SalesInterceptor(jwtToken,userRepository,permissionRepository))
                .excludePathPatterns(Arrays.asList(arr));
    }



}
