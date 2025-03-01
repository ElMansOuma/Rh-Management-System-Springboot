package com.example.personnel_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceJustificative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypePiece type;

    private String nom;
    private String fichier;
    private Date dateAjout;

    @ManyToOne
    @JoinColumn(name = "collaborateur_id")
    private Collaborateur collaborateur;
}

enum TypePiece {
    CIN, DIPLOME, AUTRE
}
