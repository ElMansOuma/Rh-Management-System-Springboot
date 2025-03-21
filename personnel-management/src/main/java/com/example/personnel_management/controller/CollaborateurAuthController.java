package com.example.personnel_management.controller;

import com.example.personnel_management.DTO.LoginUserRequest;
import com.example.personnel_management.DTO.AuthUserResponse;
import com.example.personnel_management.service.CollaborateurAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/collaborateur")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CollaborateurAuthController {

    private final CollaborateurAuthService collaborateurAuthService;

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(@RequestBody LoginUserRequest request) {
        AuthUserResponse response = collaborateurAuthService.authenticateCollaborateur(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestParam Long id, @RequestParam String newPassword) {
        collaborateurAuthService.changePassword(id, newPassword);
        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }
}