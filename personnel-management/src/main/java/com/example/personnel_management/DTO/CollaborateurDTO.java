package com.example.personnel_management.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborateurDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String cin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;

    private String lieuNaissance;
    private String adresseDomicile;
    private String cnss;
    private String origine;
    private String niveauEtude;
    private String specialite;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateEntretien;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateEmbauche;

    private String description;
    private List<PieceJustificativeDTO> piecesJustificatives;
}