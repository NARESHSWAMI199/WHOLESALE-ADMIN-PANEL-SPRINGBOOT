package com.sales.interceptors;

import com.sales.admin.repositories.PermissionRepository;
import com.sales.admin.repositories.StorePermissionsRepository;
import com.sales.admin.repositories.UserRepository;
import com.sales.jwtUtils.JwtToken;
import com.sales.wholesaler.services.WholesaleServicePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    @Autowired
    WholesaleServicePlanService wholesaleServicePlanService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        /* Paths which no need to authenticate */
        String [] unAuthorizePaths = {"/admin/auth/login",
                "/admin/auth/login/otp",
                "/admin/auth/sendOtp",
                "/admin/auth/register",
                "/wholesale/auth/login",
                "/wholesale/auth/register",
                "/wholesale/auth/login/otp",
                "/wholesale/auth/sendOtp",
                "/wholesale/auth/register",
                "/webjars/**",
                "/admin/auth/profile/**",
                "/wholesale/auth/profile/**",
                "/admin/store/image/**",
                "/admin/item/image/**",
                "/pg/callback/**",
                "/cashfree/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api-docs/**",
                "/plans/**",
                "/wholesale/address/state",
                "/wholesale/address/city/**",
                "/wholesale/store/category/**"
        };
        /* Paths which need to be authenticated but don't need to check in Interceptor due to some different conditions */
        String [] authorizedPaths = {
            "/wholesale/plan/**", "/pg/pay/**"
        };

        List<String> excludingPaths = new ArrayList<>(List.of(unAuthorizePaths));
        excludingPaths.addAll(Arrays.asList(authorizedPaths));
        registry.addInterceptor(new SalesInterceptor(jwtToken,userRepository,permissionRepository,storePermissionsRepository,wholesaleServicePlanService))
                .excludePathPatterns(excludingPaths);
    }



    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Replace with your React app's origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Add OPTIONS for preflight requests
                .allowedHeaders("*", "Authorization") // Allow all headers and specifically 'Authorization'
                .allowCredentials(true)
                .exposedHeaders("Authorization"); // Expose 'Authorization' header in response
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }


}
