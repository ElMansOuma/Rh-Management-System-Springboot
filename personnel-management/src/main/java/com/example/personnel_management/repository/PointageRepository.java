package com.example.personnel_management.repository;

import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.model.Pointage;
import com.example.personnel_management.model.Pointage.PointageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointageRepository extends JpaRepository<Pointage, Long> {
    // Find the last pointage for a specific collaborateur
    @Query("SELECT p FROM Pointage p " +
            "WHERE p.collaborateur = :collaborateur " +
            "AND p.timestamp = (SELECT MAX(p2.timestamp) FROM Pointage p2 WHERE p2.collaborateur = :collaborateur)")
    Optional<Pointage> findLastPointageByCollaborateur(@Param("collaborateur") Collaborateur collaborateur);

    // Find pointages for a collaborateur within a specific date range
    List<Pointage> findByCollaborateurAndTimestampBetweenOrderByTimestampAsc(
            Collaborateur collaborateur,
            LocalDateTime start,
            LocalDateTime end
    );

    // Count pointages by type for a collaborateur within a date range
    long countByCollaborateurAndTypeAndTimestampBetween(
            Collaborateur collaborateur,
            PointageType type,
            LocalDateTime start,
            LocalDateTime end
    );
}