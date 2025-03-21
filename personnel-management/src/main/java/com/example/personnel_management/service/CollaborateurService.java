package com.example.personnel_management.service;

import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.repository.CollaborateurRepository;
import com.example.personnel_management.DTO.CollaborateurDTO;
import com.example.personnel_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    // Récupérer tous les collaborateurs
    @Transactional(readOnly = true)
    public List<CollaborateurDTO> getAllCollaborateurs() {
        List<Collaborateur> collaborateurs = collaborateurRepository.findAll();
        return collaborateurs.stream()
                .map(this::mapToCollaborateurDTO)
                .collect(Collectors.toList());
    }

    // Pour compatibilité avec l'ancienne méthode si nécessaire
    @Transactional(readOnly = true)
    public List<Collaborateur> getAllCollaborateursEntities() {
        return collaborateurRepository.findAll();
    }

    // Récupérer un collaborateur par ID et le mapper à un DTO
    @Transactional(readOnly = true)
    public CollaborateurDTO getCollaborateurById(Long id) {
        Collaborateur collaborateur = collaborateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé pour l'ID " + id));
        return mapToCollaborateurDTO(collaborateur);
    }

    // Pour compatibilité avec l'ancienne méthode si nécessaire
    @Transactional(readOnly = true)
    public Collaborateur getCollaborateurEntityById(Long id) {
        return collaborateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé pour l'ID " + id));
    }

    // Récupérer un collaborateur avec ses pièces justificatives par ID
    @Transactional(readOnly = true)
    public Collaborateur getCollaborateurAvecPieces(Long id) {
        return collaborateurRepository.findByIdWithPieces(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé pour l'ID " + id));
    }

    // Méthode de conversion en DTO
    private CollaborateurDTO mapToCollaborateurDTO(Collaborateur collaborateur) {
        return modelMapper.map(collaborateur, CollaborateurDTO.class); // Utilise ModelMapper pour convertir
    }

    // Créer un collaborateur
    @Transactional
    public Collaborateur saveCollaborateur(@Valid Collaborateur collaborateur) {
        // Générer un mot de passe par défaut à partir des 4 derniers chiffres du CIN
        if ((collaborateur.getPassword() == null || collaborateur.getPassword().isEmpty()) && collaborateur.getCin() != null && !collaborateur.getCin().isEmpty()) {
            String cin = collaborateur.getCin();
            String defaultPassword = cin.length() >= 4
                    ? cin.substring(cin.length() - 4)
                    : cin;

            // Encoder le mot de passe
            collaborateur.setPassword(passwordEncoder.encode(defaultPassword));

            // Indiquer que le mot de passe doit être réinitialisé
            collaborateur.setResetPassword(true);
        } else if (collaborateur.getPassword() == null || collaborateur.getPassword().isEmpty()) {
            // Si le password est null/vide et le CIN également, définir un mot de passe par défaut
            collaborateur.setPassword(passwordEncoder.encode("1234"));
            collaborateur.setResetPassword(true);
        }

        return collaborateurRepository.save(collaborateur);
    }

    // Mettre à jour un collaborateur par ID
    @Transactional
    public Collaborateur updateCollaborateur(Long id, @Valid Collaborateur collaborateurDetails) {
        Collaborateur existingCollaborateur = collaborateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé avec l'ID : " + id));

        // Mise à jour des propriétés de l'entité existante
        existingCollaborateur.setNom(collaborateurDetails.getNom());
        existingCollaborateur.setPrenom(collaborateurDetails.getPrenom());
        existingCollaborateur.setCin(collaborateurDetails.getCin());
        existingCollaborateur.setDateNaissance(collaborateurDetails.getDateNaissance());
        existingCollaborateur.setLieuNaissance(collaborateurDetails.getLieuNaissance());
        existingCollaborateur.setAdresseDomicile(collaborateurDetails.getAdresseDomicile());
        existingCollaborateur.setCnss(collaborateurDetails.getCnss());
        existingCollaborateur.setOrigine(collaborateurDetails.getOrigine());
        existingCollaborateur.setNiveauEtude(collaborateurDetails.getNiveauEtude());
        existingCollaborateur.setSpecialite(collaborateurDetails.getSpecialite());
        existingCollaborateur.setDateEntretien(collaborateurDetails.getDateEntretien());
        existingCollaborateur.setDateEmbauche(collaborateurDetails.getDateEmbauche());
        existingCollaborateur.setDescription(collaborateurDetails.getDescription());

        // Mise à jour de l'état actif
        existingCollaborateur.setActive(collaborateurDetails.isActive());

        // Ne pas écraser le mot de passe existant lors d'une mise à jour
        // sauf si explicitement fourni
        if (collaborateurDetails.getPassword() != null && !collaborateurDetails.getPassword().isEmpty()) {
            existingCollaborateur.setPassword(passwordEncoder.encode(collaborateurDetails.getPassword()));
        }

        return collaborateurRepository.save(existingCollaborateur);
    }

    // Supprimer un collaborateur par ID
    @Transactional
    public void deleteCollaborateur(Long id) {
        if (!collaborateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Collaborateur non trouvé avec l'ID : " + id);
        }
        collaborateurRepository.deleteById(id);
    }

    // Méthode de mise à jour spécifique avec ses pièces justificatives
    @Transactional
    public Collaborateur mettreAJourCollaborateurAvecPieces(Long id, Collaborateur collaborateur) {
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
        existant.setActive(collaborateur.isActive());

        // Ne pas modifier le mot de passe sauf si explicitement fourni
        if (collaborateur.getPassword() != null && !collaborateur.getPassword().isEmpty()) {
            existant.setPassword(passwordEncoder.encode(collaborateur.getPassword()));
        }

        // Sauvegarde après mise à jour
        return collaborateurRepository.save(existant);
    }
}