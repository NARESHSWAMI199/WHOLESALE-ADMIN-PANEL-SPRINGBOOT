package com.sales.interceptors;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.admin.repositories.PermissionRepository;
import com.sales.admin.repositories.UserRepository;
import com.sales.dto.ErrorDto;
import com.sales.entities.User;
import com.sales.jwtUtils.JwtToken;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class SalesInterceptor implements HandlerInterceptor {


    JwtToken jwtToken;
    UserRepository userRepository;

    PermissionRepository permissionRepository;

    public SalesInterceptor(JwtToken jwtToken, UserRepository userRepository, PermissionRepository permissionRepository){
        this.jwtToken = jwtToken;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("request url : "+request.getRequestURI());
        try {
        if (token != null && (token.substring(0, 7)).equals("Bearer ")) {
            token = token.substring(7, token.length());
            String slug = jwtToken.getSlugFromToken(token);
            /** get user by slug. */
            User user = userRepository.findUserBySlug(slug);
            Set<String> permittedUrls = permissionRepository.getUserAllPermission(user.getId());
            if (user.getIsDeleted().equals("Y")) {
                sendError(response,"User is not found.",401);
                return false;
            } else if (user.getStatus().equals("D")) {
                sendError(response,"User is not active.",401);
                return false;
            }
            String requestUrI = request.getRequestURI();
            /** if user have list permission also has detail permission */
            requestUrI = requestUrI.replaceAll("detail","all");
            boolean isPermitted = false;
            for( String permission : permittedUrls) {
                if(requestUrI.contains(permission)){
                    isPermitted = true;
                    break;
                }
            }
            if (!isPermitted) {
                sendError(response, "You don't permissions to access "+requestUrI+".Please contact your administrator.", 400);
                return false;
            }
            request.setAttribute("user",user);
            return true;
        }
        sendError(response,"Invalid authorization.",401);
        return false;
        }catch (Exception e){
            sendError(response,e.getMessage(),401);
            return false;
        }
    }

    public void sendError(HttpServletResponse response ,String message, Integer status) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setStatus(status);
        ErrorDto error = new ErrorDto(message,status);
        response.getWriter().write(mapper.writeValueAsString(error));
    }





}
