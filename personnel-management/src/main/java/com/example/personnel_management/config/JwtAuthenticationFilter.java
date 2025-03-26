package com.example.personnel_management.config;

import com.example.personnel_management.service.CollaborateurUserDetailsService;
import com.example.personnel_management.service.AdminUserDetailsService;
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

    private CollaborateurUserDetailsService getCollaborateurUserDetailsService() {
        if (collaborateurUserDetailsService == null) {
            collaborateurUserDetailsService = applicationContext.getBean(CollaborateurUserDetailsService.class);
        }
        return collaborateurUserDetailsService;
    }

    private AdminUserDetailsService getAdminUserDetailsService() {
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

        // Vérification de l'en-tête Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token JWT
        jwt = authHeader.substring(7);

        try {
            // Extraction de l'identifiant utilisateur (CIN ou email)
            userIdentifier = jwtUtil.extractUsername(jwt);

            // Authentification si l'identifiant est présent et non déjà authentifié
            if (userIdentifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = null;

                // Tenter de charger comme collaborateur
                try {
                    userDetails = getCollaborateurUserDetailsService().loadUserByUsername(userIdentifier);
                } catch (UsernameNotFoundException collaborateurException) {
                    // Si pas trouvé comme collaborateur, essayer comme admin
                    try {
                        userDetails = getAdminUserDetailsService().loadUserByUsername(userIdentifier);
                    } catch (UsernameNotFoundException adminException) {
                        logger.error("Utilisateur non trouvé : {}", userIdentifier);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Utilisateur non authentifié");
                        return;
                    }
                }

                // Validation du token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification JWT : {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentification invalide");
            return;
        }

        filterChain.doFilter(request, response);
    }
}