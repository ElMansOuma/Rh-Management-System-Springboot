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
    public ResponseEntity<CollaborateurDTO> getCollaborateurById(@PathVariable Long id) {
        try {
            CollaborateurDTO collaborateurDTO = collaborateurService.getCollaborateurById(id);
            return ResponseEntity.ok(collaborateurDTO);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du collaborateur id={}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Créer un collaborateur
    @PostMapping
    public ResponseEntity<?> createCollaborateur(@RequestBody Collaborateur collaborateur) {
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors de la création du collaborateur: " + e.getMessage());
        }
    }

    // Mettre à jour un collaborateur par ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCollaborateur(@PathVariable Long id, @RequestBody Collaborateur collaborateur) {
        try {
            logger.info("Tentative de mise à jour du collaborateur id={}", id);
            Collaborateur updatedCollaborateur = collaborateurService.updateCollaborateur(id, collaborateur);
            logger.info("Collaborateur mis à jour avec succès: id={}", id);
            return ResponseEntity.ok(updatedCollaborateur);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du collaborateur id={}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors de la mise à jour du collaborateur: " + e.getMessage());
        }
    }

    // Supprimer un collaborateur par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCollaborateur(@PathVariable Long id) {
        try {
            logger.info("Tentative de suppression du collaborateur id={}", id);
            collaborateurService.deleteCollaborateur(id);
            logger.info("Collaborateur supprimé avec succès: id={}", id);
            return ResponseEntity.ok("Collaborateur supprimé avec succès.");
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du collaborateur id={}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du collaborateur: " + e.getMessage());
        }
    }
    @GetMapping("/cin/{cin}")
    public ResponseEntity<Collaborateur> getCollaborateurByCin(@PathVariable String cin) {
        return collaborateurRepository.findByCin(cin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}