package com.example.personnel_management.service;

import com.example.personnel_management.DTO.PieceJustificativeDTO;
import com.example.personnel_management.exception.ResourceNotFoundException;
import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.model.PieceJustificative;
import com.example.personnel_management.repository.CollaborateurRepository;
import com.example.personnel_management.repository.PieceJustificativeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PieceJustificativeService {

    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public PieceJustificativeService(
            PieceJustificativeRepository pieceJustificativeRepository,
            CollaborateurRepository collaborateurRepository,
            ModelMapper modelMapper,
            ObjectMapper objectMapper) {
        this.pieceJustificativeRepository = pieceJustificativeRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    public List<PieceJustificativeDTO> getAll() {
        return pieceJustificativeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PieceJustificativeDTO> getAllByStatut(String statut) {
        return pieceJustificativeRepository.findByStatut(statut).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PieceJustificativeDTO getById(Long id) {
        PieceJustificative pieceJustificative = pieceJustificativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PieceJustificative", "id", id));
        return convertToDTO(pieceJustificative);
    }

    public List<PieceJustificativeDTO> getAllByCollaborateurId(Long collaborateurId) {
        return pieceJustificativeRepository.findByCollaborateurId(collaborateurId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PieceJustificativeDTO> getAllByCollaborateurIdAndStatut(Long collaborateurId, String statut) {
        return pieceJustificativeRepository.findByCollaborateurIdAndStatut(collaborateurId, statut).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PieceJustificativeDTO create(PieceJustificativeDTO pieceJustificativeDTO) {
        // Vérifier si le collaborateur existe
        Collaborateur collaborateur = collaborateurRepository.findById(pieceJustificativeDTO.getCollaborateurId())
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur", "id", pieceJustificativeDTO.getCollaborateurId()));

        // Convertir DTO en entité
        PieceJustificative pieceJustificative = convertToEntity(pieceJustificativeDTO);
        pieceJustificative.setCollaborateur(collaborateur);
        pieceJustificative.setDateCreation(LocalDateTime.now());

        // Enregistrer l'entité
        PieceJustificative savedPieceJustificative = pieceJustificativeRepository.save(pieceJustificative);

        return convertToDTO(savedPieceJustificative);
    }

    @Transactional
    public PieceJustificativeDTO update(Long id, PieceJustificativeDTO pieceJustificativeDTO) {
        // Vérifier si la pièce justificative existe
        PieceJustificative existingPieceJustificative = pieceJustificativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PieceJustificative", "id", id));

        // Mettre à jour les champs
        existingPieceJustificative.setNom(pieceJustificativeDTO.getNom());
        existingPieceJustificative.setType(pieceJustificativeDTO.getType());
        existingPieceJustificative.setDescription(pieceJustificativeDTO.getDescription());

        // Mettre à jour le fichier si nécessaire
        if (pieceJustificativeDTO.getFichierNom() != null && pieceJustificativeDTO.getFichierUrl() != null) {
            existingPieceJustificative.setFichierNom(pieceJustificativeDTO.getFichierNom());
            existingPieceJustificative.setFichierPath(pieceJustificativeDTO.getFichierUrl());
        }

        // Enregistrer les modifications
        PieceJustificative updatedPieceJustificative = pieceJustificativeRepository.save(existingPieceJustificative);

        return convertToDTO(updatedPieceJustificative);
    }

    @Transactional
    public PieceJustificativeDTO updateFromJson(Long id, String pieceJustificativeJson) {
        try {
            // Désérialiser le JSON
            PieceJustificativeDTO pieceJustificativeDTO = objectMapper.readValue(pieceJustificativeJson, PieceJustificativeDTO.class);
            return update(id, pieceJustificativeDTO);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la désérialisation du JSON: " + e.getMessage());
        }
    }

    @Transactional
    public PieceJustificativeDTO updateStatut(Long id, String statut) {
        // Vérifier si la pièce justificative existe
        PieceJustificative pieceJustificative = pieceJustificativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PieceJustificative", "id", id));

        // Vérifier si le statut est valide
        if (!isValidStatut(statut)) {
            throw new IllegalArgumentException("Statut invalide: " + statut);
        }

        // Mettre à jour le statut
        pieceJustificative.setStatut(statut);

        // Enregistrer les modifications
        PieceJustificative updatedPieceJustificative = pieceJustificativeRepository.save(pieceJustificative);

        return convertToDTO(updatedPieceJustificative);
    }

    @Transactional
    public void delete(Long id) {
        // Vérifier si la pièce justificative existe
        PieceJustificative pieceJustificative = pieceJustificativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PieceJustificative", "id", id));

        // Supprimer la pièce justificative
        pieceJustificativeRepository.delete(pieceJustificative);
    }

    // Méthode utilitaire pour vérifier si le statut est valide
    private boolean isValidStatut(String statut) {
        return statut != null && (
                statut.equals("EN_ATTENTE") ||
                        statut.equals("VALIDE") ||
                        statut.equals("REJETE")
        );
    }

    // Méthodes de conversion DTO <-> Entité
    private PieceJustificativeDTO convertToDTO(PieceJustificative pieceJustificative) {
        PieceJustificativeDTO dto = modelMapper.map(pieceJustificative, PieceJustificativeDTO.class);
        if (pieceJustificative.getCollaborateur() != null) {
            dto.setCollaborateurId(pieceJustificative.getCollaborateur().getId());
        }
        return dto;
    }

    private PieceJustificative convertToEntity(PieceJustificativeDTO pieceJustificativeDTO) {
        PieceJustificative entity = modelMapper.map(pieceJustificativeDTO, PieceJustificative.class);

        // Ne pas définir le collaborateur ici, il sera défini dans la méthode create

        return entity;
    }
    public List<PieceJustificativeDTO> getAllByCollaborateurCin(String cin) {
        return pieceJustificativeRepository.findByCollaborateurCin(cin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PieceJustificativeDTO> getAllByCollaborateurCinAndStatut(String cin, String statut) {
        return pieceJustificativeRepository.findByCollaborateurCinAndStatut(cin, statut).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}