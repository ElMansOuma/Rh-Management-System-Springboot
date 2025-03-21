package com.example.personnel_management.controller;

import com.example.personnel_management.DTO.AuthUserResponse;
import com.example.personnel_management.DTO.LoginUserRequest;
import com.example.personnel_management.exception.ResourceNotFoundException;
import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.repository.CollaborateurRepository;
import com.example.personnel_management.service.CollaborateurAuthService;
import com.example.personnel_management.service.CollaborateurService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/user")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthUserController {

    private static final Logger logger = LoggerFactory.getLogger(AuthUserController.class);
    private final CollaborateurAuthService collaborateurAuthService;
    private final CollaborateurService collaborateurService;
    private final CollaborateurRepository collaborateurRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(@RequestBody LoginUserRequest request) {
        return ResponseEntity.ok(collaborateurAuthService.authenticateCollaborateur(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestParam Long id, @RequestParam String newPassword) {
        collaborateurAuthService.changePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    // Endpoint pour récupérer le profil par CIN (pour correspondre à votre frontend)
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam(required = false) String cin) {
        try {
            // Si le paramètre CIN est fourni (comme dans votre frontend)
            if (cin != null && !cin.isEmpty()) {
                logger.info("Recherche de profil avec CIN: {}", cin);
                Collaborateur collaborateur = collaborateurRepository.findByCin(cin)
                        .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé avec le CIN : " + cin));
                return ResponseEntity.ok(collaborateur);
            }

            // Sinon, utiliser l'authentification du contexte de sécurité
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName().equals("anonymousUser")) {
                logger.warn("Tentative d'accès au profil sans authentification");
                return ResponseEntity.status(401).body(Map.of("message", "Utilisateur non authentifié"));
            }

            String username = authentication.getName();
            logger.info("Récupération du profil pour l'utilisateur authentifié: {}", username);

            Collaborateur collaborateur = collaborateurRepository.findByCin(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé avec le CIN : " + username));

            return ResponseEntity.ok(collaborateur);
        } catch (ResourceNotFoundException e) {
            logger.error("Profil non trouvé: {}", e.getMessage());
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du profil: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Erreur lors de la récupération du profil: " + e.getMessage()));
        }
    }
}