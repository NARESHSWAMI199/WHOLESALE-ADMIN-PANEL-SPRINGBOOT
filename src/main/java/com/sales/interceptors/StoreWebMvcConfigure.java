package com.sales.interceptors;

import com.sales.admin.repositories.PermissionRepository;
import com.sales.admin.repositories.StorePermissionsRepository;
import com.sales.admin.repositories.UserRepository;
import com.sales.jwtUtils.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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

    @Autowired
    StorePermissionsRepository storePermissionsRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String arr[] = {
                "/admin/auth/login",
                "/admin/auth/login/otp",
                "/admin/auth/sendOtp",
                "/admin/auth/register",
                "/wholesale/auth/login",
                "/wholesale/auth/login/otp",
                "/wholesale/auth/sendOtp",
                "/wholesale/auth/register",
                "/webjars/**",
                "/admin/auth/profile/**",
                "/wholesale/auth/profile/**",
                "/admin/store/image/**",
                "/admin/item/image/**",
                "/pg/**",
                "/cashfree/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api-docs/**",
                "/plans/**"


        };
        registry.addInterceptor(new SalesInterceptor(jwtToken,userRepository,permissionRepository,storePermissionsRepository))
                .excludePathPatterns(Arrays.asList(arr));
    }



    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Replace with your React app's origin
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*") // Allow all headers, including your custom header
                .allowCredentials(true);
    }


}
