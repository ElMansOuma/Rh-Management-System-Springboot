package com.example.personnel_management.DTO;

import com.example.personnel_management.model.Pointage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointageDTO {
    private Long id;
    private Pointage.PointageType type;
    private LocalDateTime timestamp;

    private String cin;
    private String nom;
    private String prenom;


}