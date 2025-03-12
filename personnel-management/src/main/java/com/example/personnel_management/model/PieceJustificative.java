package com.example.personnel_management.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceJustificative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String type;
    private String fichierUrl;

    @ManyToOne
    @JoinColumn(name = "collaborateur_id")
    @JsonBackReference
    private Collaborateur collaborateur;
}