package com.sales.filters;

import com.sales.admin.repositories.GroupPermissionRepository;
import com.sales.admin.repositories.StorePermissionsRepository;
import com.sales.admin.repositories.UserRepository;
import com.sales.cachemanager.services.UserCacheService;
import com.sales.entities.AuthUser;
import com.sales.entities.SalesUser;
import com.sales.entities.User;
import com.sales.global.GlobalConstant;
import com.sales.jwtUtils.JwtToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtToken jwtUtil;
    private final UserRepository userRepository;
    private final GroupPermissionRepository groupPermissionRepository;
    private final StorePermissionsRepository storePermissionsRepository;
    private final UserCacheService userCacheService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        log.info("The request path : {}",request.getRequestURI());

        String authHeader = request.getHeader(GlobalConstant.AUTHORIZATION);
        String authHeader1 = request.getHeader(GlobalConstant.AUTHORIZATION.toLowerCase());
        log.info("The request authHeader : {},{}",authHeader,authHeader1);
        if (authHeader != null && authHeader.startsWith(GlobalConstant.AUTH_TOKEN_PREFIX)) {
            String token = authHeader.substring(7);
            log.info("The request token : {}",token);
            String slug = jwtUtil.getSlugFromToken(token);
            User user = userCacheService.getCacheUser(slug);
            if(user == null){
                user = userRepository.findUserBySlug(slug);
                List<GrantedAuthority> authorities = grantedAuthorities(user);
                user.setAuthorities(authorities);
                userCacheService.saveCacheUser(user);
            }
            AuthUser userDetails = new SalesUser(user);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, slug, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);

    }


    private List<GrantedAuthority> grantedAuthorities(User user){
        Set<String> permissions = new HashSet<>();
        if(user.getUserType().equals("S")  || user.getUserType().equals("SA")){
            permissions = groupPermissionRepository.getUserAllPermission(user.getId());
        }else if(user.getUserType().equals("W")){
            permissions = storePermissionsRepository.getAllAssignedPermissionByUserId(user.getId());
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        permissions.forEach(permission -> {
            authorities.add(new SimpleGrantedAuthority(permission));
        });
        return authorities;
    }

}
