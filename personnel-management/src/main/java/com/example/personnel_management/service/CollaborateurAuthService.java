package com.example.personnel_management.service;

import com.example.personnel_management.DTO.LoginUserRequest;
import com.example.personnel_management.DTO.AuthUserResponse;
import com.example.personnel_management.config.JwtUtil;
import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.repository.CollaborateurRepository;
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
public class CollaborateurAuthService {

    private final CollaborateurRepository collaborateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthUserResponse authenticateCollaborateur(LoginUserRequest request) {
        // Rechercher le collaborateur par CIN
        Collaborateur collaborateur = collaborateurRepository.findByCin(request.getCin())
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé avec ce CIN"));

        // Vérifier si le collaborateur est actif
        if (!collaborateur.isActive()) {
            throw new IllegalStateException("Ce compte est désactivé");
        }

        // Vérifier si l'utilisateur doit utiliser le mot de passe par défaut (4 derniers chiffres du CIN)
        if (collaborateur.isResetPassword()) {
            // Obtenir les 4 derniers caractères du CIN comme mot de passe par défaut
            String defaultPassword = collaborateur.getCin().substring(Math.max(0, collaborateur.getCin().length() - 4));

            // Comparer avec le mot de passe fourni
            if (!request.getPassword().equals(defaultPassword)) {
                throw new IllegalArgumentException("Mot de passe incorrect");
            }
        } else {
            // Si l'utilisateur a changé son mot de passe, vérifier avec le mot de passe hashé
            if (!passwordEncoder.matches(request.getPassword(), collaborateur.getPassword())) {
                throw new IllegalArgumentException("Mot de passe incorrect");
            }
        }

        // Créer un UserDetails à partir du collaborateur pour générer le token
        UserDetails userDetails = User.builder()
                .username(collaborateur.getCin())
                .password(collaborateur.getPassword())
                .authorities("ROLE_COLLABORATEUR")
                .build();

        // Générer le token JWT en utilisant l'interface UserDetails
        String token = jwtUtil.generateToken(userDetails);

        // Retourner la réponse
        return AuthUserResponse.builder()
                .id(collaborateur.getId())
                .nom(collaborateur.getNom())
                .prenom(collaborateur.getPrenom())
                .cin(collaborateur.getCin())
                .token(token)
                .resetPassword(collaborateur.isResetPassword())
                .build();
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        Collaborateur collaborateur = collaborateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé"));

        collaborateur.setPassword(passwordEncoder.encode(newPassword));
        collaborateur.setResetPassword(false);

        collaborateurRepository.save(collaborateur);
    }
}