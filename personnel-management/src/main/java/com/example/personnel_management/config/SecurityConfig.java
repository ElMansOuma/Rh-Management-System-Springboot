package com.example.personnel_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuration de sécurité pour l'application
 * Cette classe configure les règles d'authentification et d'autorisation
 */
@Configuration //classe de configuration
@EnableWebSecurity // Active la sécurité web
@EnableMethodSecurity // Permet la sécurité au niveau des méthodes (@PreAuthorize, etc.)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    // Utilisation de @Lazy pour résoudre le problème de dépendance circulaire
    public SecurityConfig(@Lazy UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configure la chaîne de filtres de sécurité
     * Définit les règles d'accès aux différentes URLs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthFilter,
            AuthenticationProvider authenticationProvider) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // Désactive la protection CSRF car nous utilisons des JWT
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configure CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Les URLs d'authentification sont publiques
                        .requestMatchers("/h2-console/**").permitAll() // Console H2 publique
                        .requestMatchers("/uploads/**").permitAll() // Permettre l'accès aux fichiers téléchargés
                        .requestMatchers("/api/pieces-justificatives/**").permitAll() // Permettre l'accès à l'endpoint de téléchargement de documents
                        .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Mode sans état (stateless) car nous utilisons JWT
                .authenticationProvider(authenticationProvider) // Notre fournisseur d'authentification
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Ajoute notre filtre JWT avant le filtre d'authentification standard
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); // Pour accéder à la console H2

        return http.build();
    }

    /**
     * Configuration CORS pour permettre les requêtes du frontend
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Autorise les requêtes depuis le frontend local
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Méthodes HTTP autorisées
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        )); // En-têtes autorisés élargis
        configuration.setAllowCredentials(true); // Autorise l'envoi de cookies
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition")); // En-têtes exposés pour téléchargements
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applique cette configuration à toutes les URLs
        return source;
    }

    /**
     * Configure le fournisseur d'authentification
     * Utilise notre service de détails utilisateur et notre encodeur de mot de passe
     */
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Service qui charge les informations utilisateur
        authProvider.setPasswordEncoder(passwordEncoder); // Encodeur pour vérifier les mots de passe
        return authProvider;
    }

    /**
     * Gestionnaire d'authentification de Spring Security
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Encodeur de mot de passe pour stocker les mots de passe de manière sécurisée
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Utilise l'algorithme BCrypt pour hasher les mots de passe
    }
}