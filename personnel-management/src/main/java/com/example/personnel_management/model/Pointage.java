package com.example.personnel_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pointage")
public class Pointage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le type de pointage est obligatoire")
    @Enumerated(EnumType.STRING)
    private PointageType type;

    @NotNull(message = "Le timestamp est obligatoire")
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collaborateur_id", nullable = false)
    private Collaborateur collaborateur;

    // Static builder method
    public static PointageBuilder builder() {
        return new PointageBuilder();
    }

    // Manual builder implementation
    public static class PointageBuilder {
        private PointageType type;
        private LocalDateTime timestamp;
        private Collaborateur collaborateur;

        public PointageBuilder type(PointageType type) {
            this.type = type;
            return this;
        }

        public PointageBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public PointageBuilder collaborateur(Collaborateur collaborateur) {
            this.collaborateur = collaborateur;
            return this;
        }

        public Pointage build() {
            Pointage pointage = new Pointage();
            pointage.setType(this.type);
            pointage.setTimestamp(this.timestamp);
            pointage.setCollaborateur(this.collaborateur);
            return pointage;
        }
    }

    public enum PointageType {
        ARRIVEE,
        DEPART
    }
}