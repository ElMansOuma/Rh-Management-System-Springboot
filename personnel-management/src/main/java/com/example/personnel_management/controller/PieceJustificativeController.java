package com.example.personnel_management.controller;

import com.example.personnel_management.DTO.PieceJustificativeDTO;
import com.example.personnel_management.service.PieceJustificativeService;
import com.example.personnel_management.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pieces-justificatives")
@CrossOrigin(origins = "http://localhost:3000")
public class PieceJustificativeController {

    private final PieceJustificativeService pieceJustificativeService;
    private final FileStorageService fileStorageService;

    @Autowired
    public PieceJustificativeController(PieceJustificativeService pieceJustificativeService,
                                        FileStorageService fileStorageService) {
        this.pieceJustificativeService = pieceJustificativeService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<List<PieceJustificativeDTO>> getAllPiecesJustificatives(
            @RequestParam(required = false) String statut) {
        if (statut != null && !statut.isEmpty()) {
            return ResponseEntity.ok(pieceJustificativeService.getAllByStatut(statut));
        }
        return ResponseEntity.ok(pieceJustificativeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PieceJustificativeDTO> getPieceJustificativeById(@PathVariable Long id) {
        return ResponseEntity.ok(pieceJustificativeService.getById(id));
    }

    @GetMapping("/api/pieces-justificatives/collaborateur/{id}")
    public ResponseEntity<List<PieceJustificativeDTO>> getPiecesByCollaborateurId(
            @PathVariable Long collaborateurId,
            @RequestParam(required = false) String statut) {
        if (statut != null && !statut.isEmpty()) {
            return ResponseEntity.ok(
                    pieceJustificativeService.getAllByCollaborateurIdAndStatut(collaborateurId, statut)
            );
        }
        return ResponseEntity.ok(pieceJustificativeService.getAllByCollaborateurId(collaborateurId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PieceJustificativeDTO> createPieceJustificative(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nom") String nom,
            @RequestParam("type") String type,
            @RequestParam("collaborateurId") Long collaborateurId,
            @RequestParam(value = "description", required = false) String description) {

        try {
            // Enregistrer le fichier
            String fileName = fileStorageService.storeFile(file);

            // Créer l'URL du fichier
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/")
                    .path(fileName)
                    .toUriString();

            // Créer la pièce justificative
            PieceJustificativeDTO pieceJustificativeDTO = new PieceJustificativeDTO();
            pieceJustificativeDTO.setNom(nom);
            pieceJustificativeDTO.setType(type);
            pieceJustificativeDTO.setDescription(description);
            pieceJustificativeDTO.setCollaborateurId(collaborateurId);
            pieceJustificativeDTO.setFichierNom(fileName);
            pieceJustificativeDTO.setFichierUrl(fileDownloadUri);
            pieceJustificativeDTO.setStatut("EN_ATTENTE"); // Statut par défaut

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(pieceJustificativeService.create(pieceJustificativeDTO));
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de la pièce justificative: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PieceJustificativeDTO> updatePieceJustificative(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("pieceJustificative") String pieceJustificativeJson) {

        try {
            PieceJustificativeDTO existingPiece = pieceJustificativeService.getById(id);
            // Mise à jour depuis le JSON
            PieceJustificativeDTO updatedPiece = pieceJustificativeService.updateFromJson(id, pieceJustificativeJson);

            // Si un nouveau fichier est fourni, le gérer
            if (file != null && !file.isEmpty()) {
                // Supprimer l'ancien fichier si nécessaire
                if (existingPiece.getFichierNom() != null) {
                    fileStorageService.deleteFile(existingPiece.getFichierNom());
                }

                // Stocker le nouveau fichier
                String fileName = fileStorageService.storeFile(file);

                // Mettre à jour l'URL du fichier
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/files/")
                        .path(fileName)
                        .toUriString();

                updatedPiece.setFichierNom(fileName);
                updatedPiece.setFichierUrl(fileDownloadUri);

                // Sauvegarder les changements
                updatedPiece = pieceJustificativeService.update(id, updatedPiece);
            }

            return ResponseEntity.ok(updatedPiece);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la pièce justificative: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<PieceJustificativeDTO> updateStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> statutRequest) {

        String nouveauStatut = statutRequest.get("statut");
        if (nouveauStatut == null || nouveauStatut.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        PieceJustificativeDTO updated = pieceJustificativeService.updateStatut(id, nouveauStatut);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deletePieceJustificative(@PathVariable Long id) {
        PieceJustificativeDTO piece = pieceJustificativeService.getById(id);

        // Supprimer le fichier associé
        if (piece.getFichierNom() != null) {
            fileStorageService.deleteFile(piece.getFichierNom());
        }

        pieceJustificativeService.delete(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        // Charger le fichier comme une ressource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Déterminer le type de contenu
        String contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}