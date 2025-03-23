package com.example.personnel_management.controller;

import com.example.personnel_management.DTO.CollaborateurDTO;
import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.repository.CollaborateurRepository;
import com.example.personnel_management.service.CollaborateurService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/collaborateurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CollaborateurController {

    private static final Logger logger = LoggerFactory.getLogger(CollaborateurController.class);
    private final CollaborateurService collaborateurService;
    private final ModelMapper modelMapper;
    private final CollaborateurRepository collaborateurRepository;

    // Récupérer tous les collaborateurs avec DTO
    @GetMapping
    public ResponseEntity<List<CollaborateurDTO>> getAllCollaborateurs() {
        List<CollaborateurDTO> collaborateurs = collaborateurService.getAllCollaborateurs();
        return ResponseEntity.ok(collaborateurs);
    }

    // Récupérer un collaborateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getCollaborateurById(@PathVariable Long id) {
        try {
            CollaborateurDTO collaborateurDTO = collaborateurService.getCollaborateurById(id);
            return ResponseEntity.ok(collaborateurDTO);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du collaborateur id={}: {}", id, e.getMessage());

            // Création d'une réponse d'erreur structurée
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "NOT_FOUND");
            errorResponse.put("message", "Collaborateur non trouvé pour l'ID " + id);
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Créer un collaborateur
    @PostMapping
    public ResponseEntity<Object> createCollaborateur(@RequestBody Collaborateur collaborateur) {
        try {
            logger.info("Tentative de création d'un collaborateur: {}", collaborateur);

            // Validation basique
            if (collaborateur == null) {
                logger.error("Erreur: objet collaborateur est null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("L'objet collaborateur ne peut pas être null");
            }

            Collaborateur savedCollaborateur = collaborateurService.saveCollaborateur(collaborateur);
            CollaborateurDTO collaborateurDTO = modelMapper.map(savedCollaborateur, CollaborateurDTO.class);

            logger.info("Collaborateur créé avec succès: id={}", savedCollaborateur.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(collaborateurDTO);
        } catch (Exception e) {
            logger.error("Erreur lors de la création du collaborateur: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "BAD_REQUEST");
            errorResponse.put("message", "Erreur lors de la création du collaborateur");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Mettre à jour un collaborateur par ID
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCollaborateur(@PathVariable Long id, @RequestBody Collaborateur collaborateur) {
        try {
            logger.info("Tentative de mise à jour du collaborateur id={}", id);
            Collaborateur updatedCollaborateur = collaborateurService.updateCollaborateur(id, collaborateur);
            CollaborateurDTO collaborateurDTO = modelMapper.map(updatedCollaborateur, CollaborateurDTO.class);
            logger.info("Collaborateur mis à jour avec succès: id={}", id);
            return ResponseEntity.ok(collaborateurDTO);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du collaborateur id={}: {}", id, e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "BAD_REQUEST");
            errorResponse.put("message", "Erreur lors de la mise à jour du collaborateur id=" + id);
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Supprimer un collaborateur par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCollaborateur(@PathVariable Long id) {
        try {
            logger.info("Tentative de suppression du collaborateur id={}", id);
            collaborateurService.deleteCollaborateur(id);
            logger.info("Collaborateur supprimé avec succès: id={}", id);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Collaborateur supprimé avec succès");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du collaborateur id={}: {}", id, e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Erreur lors de la suppression du collaborateur id=" + id);
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/cin/{cin}")
    public ResponseEntity<Object> getCollaborateurByCin(@PathVariable String cin) {
        try {
            return collaborateurRepository.findByCin(cin)
                    .map(collaborateur -> {
                        CollaborateurDTO collaborateurDTO = modelMapper.map(collaborateur, CollaborateurDTO.class);
                        return ResponseEntity.ok((Object)collaborateurDTO);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("status", "NOT_FOUND");
                        errorResponse.put("message", "Collaborateur non trouvé avec le CIN " + cin);

                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    });
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du collaborateur par CIN={}: {}", cin, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Erreur lors de la récupération du collaborateur par CIN " + cin);
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}