package com.example.personnel_management.controller;

import com.example.personnel_management.DTO.PointageDTO;
import com.example.personnel_management.DTO.PointageRequest;
import com.example.personnel_management.service.PointageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pointage")
@PreAuthorize("hasRole('USER')")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PointageController {
    private final PointageService pointageService;

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
}