package com.example.personnel_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "absences")
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_absence", nullable = false)
    private AbsenceType typeAbsence;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AbsenceStatus status;

    @Column(name = "commentaire")
    private String commentaire;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "collaborateur_id", nullable = false)
    private Collaborateur collaborateur;

    public enum AbsenceType {
        CONGE_PAYE, MALADIE, RTT, SANS_SOLDE, AUTRE
    }

    public enum AbsenceStatus {
        EN_ATTENTE, APPROUVE, REFUSE
    }
}