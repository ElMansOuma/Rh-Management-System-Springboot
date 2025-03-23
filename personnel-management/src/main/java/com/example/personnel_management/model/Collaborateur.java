package com.example.personnel_management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "collaborateur")
public class Collaborateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "Le CIN est obligatoire")
    @Column(unique = true)
    private String cin;

    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate dateNaissance;

    @NotBlank(message = "Le lieu de naissance est obligatoire")
    private String lieuNaissance;

    @NotBlank(message = "L'adresse domicile est obligatoire")
    private String adresseDomicile;

    private String cnss;
    private String origine;
    private String niveauEtude;
    private String specialite;
    private LocalDate dateEntretien;
    private LocalDate dateEmbauche;
    private String description;
    private String password;

    @Column(nullable = false)
    private boolean resetPassword = true;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "collaborateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnoreProperties("collaborateur")
    private List<PieceJustificative> piecesJustificatives = new ArrayList<>();

    // Helper method to add a piece justificative
    public void addPieceJustificative(PieceJustificative pieceJustificative) {
        piecesJustificatives.add(pieceJustificative);
        pieceJustificative.setCollaborateur(this);
    }

    // Helper method to remove a piece justificative
    public void removePieceJustificative(PieceJustificative pieceJustificative) {
        piecesJustificatives.remove(pieceJustificative);
        pieceJustificative.setCollaborateur(null);
    }
}