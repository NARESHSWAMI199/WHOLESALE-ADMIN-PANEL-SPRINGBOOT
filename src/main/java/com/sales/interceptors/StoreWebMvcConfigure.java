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
                "/wholesale/store/category/**",
                "/wholesale/store/subcategory/**",
                "/wholesale/auth/validate-otp",
                "/wholesale/plan/all",
                "/admin/auth/profile/**",
                "/index",
                "/chat2",
                "/chat/images/**",
                "/js/**",
                "/css/**",
                "/images/**",
        };
        /* Paths which need to be authenticated but don't need to check in Interceptor due to some different conditions */
        String [] authorizedPaths = {
            "/wholesale/plan/my-plans",
            "/wholesale/plan/is-active",
            "/pg/pay/**",
            "/wholesale/store/add",
            "/wholesale/auth/detail",
            "/future/plans/**"
        };

        List<String> excludingPaths = new ArrayList<>(List.of(unAuthorizePaths));
        excludingPaths.addAll(Arrays.asList(authorizedPaths));
        registry.addInterceptor(new SalesInterceptor(jwtToken,userRepository,permissionRepository,storePermissionsRepository,wholesaleServicePlanService))
                .excludePathPatterns(excludingPaths);
    }



    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000","http://localhost:3001,http://192.168.1.3:3000,http://192.168.1.3:3001") // Replace with your React app's origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Add OPTIONS for preflight requests
                .allowedHeaders("*", "Authorization","X-Username") // Allow all headers and specifically 'Authorization'
                .allowCredentials(true)
                .exposedHeaders("Authorization"); // Expose 'Authorization' header in response
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }


}
