package com.example.personnel_management.controller;

import com.example.personnel_management.DTO.PieceJustificativeDTO;
import com.example.personnel_management.service.PieceJustificativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Ajouter plusieurs pièces justificatives en une seule requête
    @PostMapping("/batch/{collaborateurId}")
    public ResponseEntity<List<PieceJustificativeDTO>> addMultiplePieces(
            @PathVariable Long collaborateurId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("types") List<String> types) {

        try {
            if (files.size() != types.size()) {
                return ResponseEntity.badRequest().body(null);
            }

            List<PieceJustificativeDTO> pieces = new ArrayList<>();

            for (int i = 0; i < files.size(); i++) {
                PieceJustificativeDTO pieceDTO = pieceJustificativeService.addPieceJustificative(
                        collaborateurId, types.get(i), files.get(i));
                pieces.add(pieceDTO);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(pieces);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Supprimer une pièce justificative
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePieceJustificative(@PathVariable Long id) {
        try {
            pieceJustificativeService.deletePieceJustificative(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<PieceJustificativeDTO> updatePieceJustificative(
            @PathVariable Long id,
            @RequestParam("type") String type,
            @RequestParam("file") MultipartFile file) {
        try {
            PieceJustificativeDTO pieceDTO = pieceJustificativeService.updatePieceJustificative(id, type, file);
            return ResponseEntity.ok(pieceDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
