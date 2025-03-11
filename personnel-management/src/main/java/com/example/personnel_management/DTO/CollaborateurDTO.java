package com.example.personnel_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborateurDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String cin;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String adresseDomicile;
    private String cnss;
    private String origine;
    private String niveauEtude;
    private String specialite;
    private LocalDate dateEntretien;
    private LocalDate dateEmbauche;
    private String description;
    private List<PieceJustificativeDTO> piecesJustificatives;
}