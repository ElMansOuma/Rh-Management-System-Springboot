package com.example.personnel_management.DTO;
import com.fasterxml.jackson.annotation.JsonFormat;


import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)

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