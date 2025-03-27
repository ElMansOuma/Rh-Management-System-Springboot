package com.example.personnel_management.controller;

import com.example.personnel_management.DTO.PointageDTO;
import com.example.personnel_management.DTO.PointageRequest;
import com.example.personnel_management.service.PointageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pointage")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PointageController {
    private final PointageService pointageService;
    private static final Logger logger = LoggerFactory.getLogger(PointageController.class);

    @PostMapping
    public ResponseEntity<PointageDTO> enregistrerPointage(
            @Valid @RequestBody PointageRequest request,
            @RequestParam String cin
    ) {
        PointageDTO pointage = pointageService.enregistrerPointage(request, cin);
        return ResponseEntity.ok(pointage);
    }

    @GetMapping("/today")
    public ResponseEntity<PointageDTO> getTodayPointage(
            @RequestParam String cin
    ) {
        PointageDTO lastPointage = pointageService.getDernierPointage(cin);
        return lastPointage != null
                ? ResponseEntity.ok(lastPointage)
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<PointageDTO>> getUserPointages(
            @RequestParam String cin,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") String month,
            @RequestParam(required = false) Integer year
    ) {
        List<PointageDTO> pointages = pointageService.getUserPointages(cin, month, year);
        return ResponseEntity.ok(pointages);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllPointages(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer year
    ) {
        // Validate month format if provided
        if (month != null) {
            if (month.length() < 1 || month.length() > 2) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Le mois doit être au format 1-2 chiffres"));
            }

            // Normalize month to two digits
            month = month.length() == 1 ? "0" + month : month;
        }

        // Validate year range if provided
        if (year != null && (year < 2000 || year > 2100)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Année invalide"));
        }

        try {
            List<?> pointages = pointageService.getAllPointagesForAdmin(month, year);
            return ResponseEntity.ok(pointages);
        } catch (Exception e) {
            logger.error("Error retrieving admin pointages", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la récupération des pointages: " + e.getMessage()));
        }
    }
}