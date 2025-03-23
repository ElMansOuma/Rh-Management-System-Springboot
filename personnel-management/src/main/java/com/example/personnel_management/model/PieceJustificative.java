package com.example.personnel_management.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pieces_justificatives")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieceJustificative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "fichier_nom")
    private String fichierNom;

    @Column(name = "fichier_path")
    private String fichierPath;

    @Column(nullable = false)
    private String statut = "EN_ATTENTE"; // Valeurs possibles: EN_ATTENTE, VALIDE, REJETE

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collaborateur_id", nullable = false)
    @JsonBackReference
    private Collaborateur collaborateur;
}
