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

    // Récupérer un collaborateur avec ses pièces justificatives par ID
    public Collaborateur getCollaborateurAvecPieces(Long id) {
        return collaborateurRepository.findByIdWithPieces(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé pour l'ID " + id));
    }

    // Méthode de conversion en DTO
    private CollaborateurDTO mapToCollaborateurDTO(Collaborateur collaborateur) {
        return modelMapper.map(collaborateur, CollaborateurDTO.class); // Utilise ModelMapper pour convertir
    }

    // Créer un collaborateur
    public Collaborateur saveCollaborateur(@Valid Collaborateur collaborateur) {
        return collaborateurRepository.save(collaborateur);
    }

    // Mettre à jour un collaborateur par ID
    public Collaborateur updateCollaborateur(Long id, @Valid Collaborateur collaborateur) {
        Collaborateur existingCollaborateur = collaborateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé avec l'ID : " + id));

        // Mise à jour des propriétés de l'entité existante
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

    // Méthode de mise à jour spécifique avec ses pièces justificatives
    @Transactional
    public Collaborateur mettreAJourCollaborateur(Long id, Collaborateur collaborateur) {
        Collaborateur existant = collaborateurRepository.findByIdWithPieces(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé pour l'ID " + id));

        // Mise à jour de l'entité avec les nouvelles données
        existant.setNom(collaborateur.getNom());
        existant.setPrenom(collaborateur.getPrenom());
        existant.setCin(collaborateur.getCin());
        existant.setDateNaissance(collaborateur.getDateNaissance());
        existant.setLieuNaissance(collaborateur.getLieuNaissance());
        existant.setAdresseDomicile(collaborateur.getAdresseDomicile());
        existant.setCnss(collaborateur.getCnss());
        existant.setOrigine(collaborateur.getOrigine());
        existant.setNiveauEtude(collaborateur.getNiveauEtude());
        existant.setSpecialite(collaborateur.getSpecialite());
        existant.setDateEntretien(collaborateur.getDateEntretien());
        existant.setDateEmbauche(collaborateur.getDateEmbauche());
        existant.setDescription(collaborateur.getDescription());

        // Sauvegarde après mise à jour
        return collaborateurRepository.save(existant);
    }

}
