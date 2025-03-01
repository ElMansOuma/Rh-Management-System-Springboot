package com.example.personnel_management.service;

import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.repository.CollaborateurRepository;
import com.example.personnel_management.DTO.CollaborateurDTO;
import com.example.personnel_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollaborateurService {

    private final CollaborateurRepository collaborateurRepository;
    private final ModelMapper modelMapper;

    // Récupérer tous les collaborateurs
    @Transactional(readOnly = true)
    public List<CollaborateurDTO> getAllCollaborateurs() {
        List<Collaborateur> collaborateurs = collaborateurRepository.findAll();
        return collaborateurs.stream()
                .map(this::mapToCollaborateurDTO)
                .collect(Collectors.toList());
    }

    // Récupérer un collaborateur par ID et le mapper à un DTO
    @Transactional(readOnly = true)
    public CollaborateurDTO getCollaborateurById(Long id) {
        Collaborateur collaborateur = collaborateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé pour l'ID " + id));
        return mapToCollaborateurDTO(collaborateur);
    }

    // Méthode de conversion de l'entité Collaborateur en DTO
    private CollaborateurDTO mapToCollaborateurDTO(Collaborateur collaborateur) {
        return modelMapper.map(collaborateur, CollaborateurDTO.class);
    }

    // Créer un collaborateur
    public Collaborateur saveCollaborateur(@Valid Collaborateur collaborateur) {
        return collaborateurRepository.save(collaborateur);
    }

    // Mettre à jour un collaborateur par ID
    public Collaborateur updateCollaborateur(Long id, @Valid Collaborateur collaborateur) {
        Collaborateur existingCollaborateur = collaborateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé avec l'ID : " + id));

        existingCollaborateur.setNom(collaborateur.getNom());
        existingCollaborateur.setPrenom(collaborateur.getPrenom());
        existingCollaborateur.setCin(collaborateur.getCin());
        existingCollaborateur.setDateNaissance(collaborateur.getDateNaissance());
        existingCollaborateur.setLieuNaissance(collaborateur.getLieuNaissance());
        existingCollaborateur.setAdresseDomicile(collaborateur.getAdresseDomicile());
        existingCollaborateur.setCnss(collaborateur.getCnss());
        existingCollaborateur.setOrigine(collaborateur.getOrigine());
        existingCollaborateur.setNiveauEtude(collaborateur.getNiveauEtude());
        existingCollaborateur.setSpecialite(collaborateur.getSpecialite());
        existingCollaborateur.setDateEntretien(collaborateur.getDateEntretien());
        existingCollaborateur.setDateEmbauche(collaborateur.getDateEmbauche());
        existingCollaborateur.setDescription(collaborateur.getDescription());

        return collaborateurRepository.save(existingCollaborateur);
    }

    // Supprimer un collaborateur par ID
    public void deleteCollaborateur(Long id) {
        if (!collaborateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Collaborateur non trouvé avec l'ID : " + id);
        }
        collaborateurRepository.deleteById(id);
    }
}