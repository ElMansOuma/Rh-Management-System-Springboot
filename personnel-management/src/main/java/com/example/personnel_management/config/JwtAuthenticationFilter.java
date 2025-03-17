package com.example.personnel_management.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre d'authentification JWT
 * Ce filtre intercepte chaque requête pour vérifier le token JWT
 */
@Component // Indique à Spring que c'est un composant à instancier automatiquement
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ApplicationContext applicationContext;
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ApplicationContext applicationContext) {
        this.jwtUtil = jwtUtil; // Utilitaire pour manipuler les tokens JWT
        this.applicationContext = applicationContext; // Contexte Spring pour résoudre les dépendances
    }

    /**
     * Méthode pour obtenir le service UserDetailsService de manière paresseuse (lazy)
     * Résout le problème de dépendance circulaire
     */
    private UserDetailsService getUserDetailsService() {
        if (userDetailsService == null) {
            userDetailsService = applicationContext.getBean(UserDetailsService.class);
        }
        return userDetailsService;
    }

    /**
     * Méthode principale du filtre exécutée pour chaque requête
     * Vérifie le token JWT et authentifie l'utilisateur si le token est valide
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); // Récupère l'en-tête Authorization
        final String jwt;
        final String userEmail;

        // Si l'en-tête n'existe pas ou n'est pas au format Bearer, passe au filtre suivant
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrait le token JWT (enlève "Bearer ")
        jwt = authHeader.substring(7);
        try {
            // Extrait l'email de l'utilisateur du token
            userEmail = jwtUtil.extractUsername(jwt);

            // Si l'email est présent et que l'utilisateur n'est pas déjà authentifié
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Charge les détails de l'utilisateur depuis la base de données
                UserDetails userDetails = getUserDetailsService().loadUserByUsername(userEmail);

                // Vérifie si le token est valide pour cet utilisateur
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Crée un objet d'authentification avec les droits de l'utilisateur
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    // Ajoute des détails sur la requête
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Enregistre l'authentification dans le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Impossible de définir l'authentification dans le contexte de sécurité", e);
        }

        // Continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}