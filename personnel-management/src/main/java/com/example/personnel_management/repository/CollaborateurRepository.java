package com.example.personnel_management.repository;

import com.example.personnel_management.model.Collaborateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollaborateurRepository extends JpaRepository<Collaborateur, Long> {

    Optional<Collaborateur> findByCin(String cin);

    // Méthode existante pour récupérer un collaborateur avec ses pièces
    @Query("SELECT c FROM Collaborateur c LEFT JOIN FETCH c.piecesJustificatives WHERE c.id = :id")
    Optional<Collaborateur> findByIdWithPieces(@Param("id") Long id);
}