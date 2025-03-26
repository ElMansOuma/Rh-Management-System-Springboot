package com.example.personnel_management.controller;

import com.example.personnel_management.DTO.PointageDTO;
import com.example.personnel_management.DTO.PointageRequest;
import com.example.personnel_management.DTO.PointageResumeDTO;
import com.example.personnel_management.service.PointageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pointage")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PointageController {
    private final PointageService pointageService;

    @PostMapping("/{cin}")
    public ResponseEntity<PointageDTO> enregistrerPointage(
            @PathVariable String cin,
            @Valid @RequestBody PointageRequest request
    ) {
        PointageDTO pointage = pointageService.enregistrerPointage(request, cin);
        return ResponseEntity.ok(pointage);
    }

    @GetMapping("/last/{cin}")
    public ResponseEntity<PointageDTO> getDernierPointage(
            @PathVariable String cin
    ) {
        PointageDTO lastPointage = pointageService.getDernierPointage(cin);
        return lastPointage != null
                ? ResponseEntity.ok(lastPointage)
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/resume/{cin}")
    public ResponseEntity<PointageResumeDTO> getPointageResume(
            @PathVariable String cin
    ) {
        PointageResumeDTO resume = pointageService.getPointageResume(cin);
        return ResponseEntity.ok(resume);
    }
}