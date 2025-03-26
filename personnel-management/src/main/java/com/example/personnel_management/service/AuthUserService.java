package com.example.personnel_management.service;

import com.example.personnel_management.DTO.AuthUserResponse;
import com.example.personnel_management.DTO.LoginUserRequest;
import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.repository.CollaborateurRepository;
import com.example.personnel_management.config.JwtUtil;
import com.example.personnel_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final CollaborateurRepository collaborateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Authentification d'un collaborateur avec génération de token JWT
     */
    public AuthUserResponse login(LoginUserRequest request) {
        Collaborateur collaborateur = collaborateurRepository.findByCin(request.getCin())
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé avec le CIN : " + request.getCin()));

        // Vérification du mot de passe
        if (!passwordEncoder.matches(request.getPassword(), collaborateur.getPassword())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }

        // Créer les UserDetails pour générer le token
        UserDetails userDetails = new User(
                collaborateur.getCin(),
                collaborateur.getPassword(),
                Collections.emptyList() // Vous pouvez ajouter des rôles/autorités ici si nécessaire
        );

        // Générer le token JWT
        String token = jwtUtil.generateToken(userDetails);

        // Créer la réponse
        return AuthUserResponse.builder()
                .id(collaborateur.getId())
                .nom(collaborateur.getNom())
                .prenom(collaborateur.getPrenom())
                .cin(collaborateur.getCin())
                .resetPassword(collaborateur.isResetPassword())
                .token(token) // Ajout du token JWT
                .build();
    }

    /**
     * Changement de mot de passe
     */
    @Transactional
    public void changePassword(Long id, String newPassword) {
        Collaborateur collaborateur = collaborateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé avec l'ID : " + id));

        // Encoder et sauvegarder le nouveau mot de passe
        collaborateur.setPassword(passwordEncoder.encode(newPassword));
        collaborateur.setResetPassword(false); // Réinitialiser le flag

        collaborateurRepository.save(collaborateur);
    }
}