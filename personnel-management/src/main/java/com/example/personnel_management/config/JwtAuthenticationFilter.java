package com.example.personnel_management.config;

import com.example.personnel_management.service.AdminUserDetailsService;
import com.example.personnel_management.service.CollaborateurUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final ApplicationContext applicationContext;
    private CollaborateurUserDetailsService collaborateurUserDetailsService;
    private AdminUserDetailsService adminUserDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ApplicationContext applicationContext) {
        this.jwtUtil = jwtUtil;
        this.applicationContext = applicationContext;
    }

    private synchronized CollaborateurUserDetailsService getCollaborateurUserDetailsService() {
        if (collaborateurUserDetailsService == null) {
            collaborateurUserDetailsService = applicationContext.getBean(CollaborateurUserDetailsService.class);
        }
        return collaborateurUserDetailsService;
    }

    private synchronized AdminUserDetailsService getAdminUserDetailsService() {
        if (adminUserDetailsService == null) {
            adminUserDetailsService = applicationContext.getBean(AdminUserDetailsService.class);
        }
        return adminUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userIdentifier;

        // Check Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token
        jwt = authHeader.substring(7);

        try {
            // Extract user identifier (username or email)
            userIdentifier = jwtUtil.extractUsername(jwt);

            // Extract roles from token
            List<String> roles = jwtUtil.extractRoles(jwt);
            logger.info("Extracted roles: {}", roles);

            // Authenticate if identifier is present and not already authenticated
            if (userIdentifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = null;

                // Attempt to load as collaborateur or admin
                try {
                    userDetails = getCollaborateurUserDetailsService().loadUserByUsername(userIdentifier);
                } catch (UsernameNotFoundException collaborateurException) {
                    try {
                        userDetails = getAdminUserDetailsService().loadUserByUsername(userIdentifier);
                    } catch (UsernameNotFoundException adminException) {
                        logger.error("User not found: {}", userIdentifier);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Authentication failed");
                        return;
                    }
                }

                // Validate token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.info("Authentication successful for user: {}", userIdentifier);
                    logger.info("User Authorities: {}", userDetails.getAuthorities());
                }
            }
        } catch (Exception e) {
            logger.error("JWT Authentication error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid authentication");
            return;
        }

        filterChain.doFilter(request, response);
    }
}