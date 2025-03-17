package com.example.personnel_management.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilitaire pour la gestion des tokens JWT
 * Cette classe permet de créer, valider et extraire des informations des tokens JWT
 */
@Component // Indique à Spring que c'est un composant à instancier automatiquement
public class JwtUtil {

    @Value("${app.jwt.secret}") // Récupère la valeur depuis application.properties/yml
    private String secret; // Clé secrète pour signer les tokens

    @Value("${app.jwt.expiration}")
    private long expiration; // Durée de validité des tokens en millisecondes

    /**
     * Crée une clé de signature à partir du secret
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes); // Crée une clé HMAC-SHA
    }

    /**
     * Extrait le nom d'utilisateur (email) du token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Méthode générique pour extraire une information spécifique du token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait toutes les informations (claims) du token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Utilise la clé pour vérifier la signature
                .build()
                .parseClaimsJws(token) // Analyse le token
                .getBody(); // Récupère le contenu
    }

    /**
     * Vérifie si le token est expiré
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Génère un token pour un utilisateur
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities()); // Ajoute les rôles/permissions de l'utilisateur
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crée un token JWT avec les informations fournies
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Données personnalisées
                .setSubject(subject) // Identifiant de l'utilisateur (email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date de création
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Date d'expiration
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Signe avec la clé et l'algorithme HS256
                .compact(); // Génère le token en format String
    }

    /**
     * Valide un token pour un utilisateur spécifique
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Vérifie que c'est le bon utilisateur et que le token n'est pas expiré
    }
}