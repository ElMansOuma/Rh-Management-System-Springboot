package com.example.personnel_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieceJustificativeDTO {
    private Long id;
    private String nom;
    private String type;
    private String description;
    private String fichierNom;
    private String fichierUrl;
    private String statut;
    private LocalDateTime dateCreation;
    private Long collaborateurId;
}