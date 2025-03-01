package com.example.personnel_management.controller;

import com.example.personnel_management.DTO.CollaborateurDTO;
import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.service.CollaborateurService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/collaborateurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CollaborateurController {

    private final CollaborateurService collaborateurService;
    private final ModelMapper modelMapper;

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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Retourner 404 si collaborateur non trouvé
        }
    }

    // Créer un collaborateur
    @PostMapping
    public ResponseEntity<CollaborateurDTO> createCollaborateur(@RequestBody Collaborateur collaborateur) {
        try {
            Collaborateur savedCollaborateur = collaborateurService.saveCollaborateur(collaborateur);
            CollaborateurDTO collaborateurDTO = modelMapper.map(savedCollaborateur, CollaborateurDTO.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(collaborateurDTO);  // Retourner 201 (Created)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();  // Retourner 400 (Bad Request) en cas d'erreur
        }
    }

    // Mettre à jour un collaborateur par ID
    @PutMapping("/{id}")
    public ResponseEntity<Collaborateur> updateCollaborateur(@PathVariable Long id, @RequestBody Collaborateur collaborateur) {
        Collaborateur updatedCollaborateur = collaborateurService.updateCollaborateur(id, collaborateur);
        return ResponseEntity.ok(updatedCollaborateur);
    }

    // Supprimer un collaborateur par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCollaborateur(@PathVariable Long id) {
        try {
            collaborateurService.deleteCollaborateur(id);
            return ResponseEntity.ok("Collaborateur supprimé avec succès."); // Retourner 200 avec un message
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du collaborateur : " + e.getMessage()); // Retourner 500 avec un message d'erreur
        }
    }
}
