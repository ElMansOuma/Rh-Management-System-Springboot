package com.example.personnel_management.controller;

import com.example.personnel_management.DTO.PieceJustificativeDTO;
import com.example.personnel_management.service.PieceJustificativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pieces-justificatives")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PieceJustificativeController {

    private final PieceJustificativeService pieceJustificativeService;

    // Récupérer toutes les pièces justificatives d'un collaborateur
    @GetMapping("/collaborateur/{collaborateurId}")
    public ResponseEntity<List<PieceJustificativeDTO>> getPiecesByCollaborateur(@PathVariable Long collaborateurId) {
        List<PieceJustificativeDTO> pieces = pieceJustificativeService.getPiecesByCollaborateur(collaborateurId);
        return ResponseEntity.ok(pieces);
    }

    // Ajouter une pièce justificative
    @PostMapping("/collaborateur/{collaborateurId}")
    public ResponseEntity<PieceJustificativeDTO> addPieceJustificative(
            @PathVariable Long collaborateurId,
            @RequestParam("type") String type,
            @RequestParam("file") MultipartFile file) {
        try {
            PieceJustificativeDTO pieceDTO = pieceJustificativeService.addPieceJustificative(collaborateurId, type, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(pieceDTO);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Supprimer une pièce justificative
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePieceJustificative(@PathVariable Long id) {
        try {
            pieceJustificativeService.deletePieceJustificative(id);
            return ResponseEntity.ok("Pièce justificative supprimée avec succès.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression de la pièce justificative : " + e.getMessage());
        }
    }
}