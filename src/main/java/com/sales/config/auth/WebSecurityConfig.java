package com.sales.config.auth;


import com.sales.filters.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtFilter jwtFilter;
    private final SalesAuthenticationManager authenticationManager;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

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
                /* Paths which need to be authenticated but don't need to check in Interceptor due to some different conditions */
                "/wholesale/plan/my-plans",
                "/wholesale/plan/is-active",
                "/pg/pay/**",
                "/wholesale/store/add",
                "/wholesale/auth/detail",
                "/future/plans/**",
                "/wholesale/wallet/**"
        };

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(unAuthorizePaths).permitAll()
                        .anyRequest().authenticated()
                ).authenticationManager(authenticationManager)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}