package com.example.personnel_management.repository;

import com.example.personnel_management.model.PieceJustificative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, Long> {
    List<PieceJustificative> findByCollaborateurId(Long collaborateurId);
    List<PieceJustificative> findByStatut(String statut);
    List<PieceJustificative> findByCollaborateurIdAndStatut(Long collaborateurId, String statut);
}