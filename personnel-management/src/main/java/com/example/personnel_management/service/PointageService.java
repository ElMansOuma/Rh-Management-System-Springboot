package com.example.personnel_management.service;

import com.example.personnel_management.DTO.PointageDTO;
import com.example.personnel_management.DTO.PointageRequest;
import com.example.personnel_management.DTO.PointageResumeDTO;
import com.example.personnel_management.exception.BusinessRuleException;
import com.example.personnel_management.exception.ResourceNotFoundException;
import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.model.Pointage;
import com.example.personnel_management.model.Pointage.PointageType;
import com.example.personnel_management.repository.CollaborateurRepository;
import com.example.personnel_management.repository.PointageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointageService {
    private final PointageRepository pointageRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final ModelMapper modelMapper;

    /**
     * Enregistre un nouveau pointage pour un collaborateur
     *
     * @param request Requête de pointage
     * @param cin Numéro CIN du collaborateur
     * @return DTO du pointage enregistré
     * @throws ResourceNotFoundException Si le collaborateur n'est pas trouvé
     * @throws BusinessRuleException Si les règles de pointage ne sont pas respectées
     */
    @Transactional
    public PointageDTO enregistrerPointage(PointageRequest request, String cin) {
        // Validation des paramètres d'entrée
        validateInputParameters(request, cin);

        // Récupérer le collaborateur
        Collaborateur collaborateur = findCollaborateurByCin(cin);

        // Récupérer le dernier pointage
        Optional<Pointage> lastPointageOptional = pointageRepository
                .findLastPointageByCollaborateur(collaborateur);

        // Valider le nouveau pointage par rapport au dernier pointage
        validateNewPointage(request, lastPointageOptional);

        // Créer et enregistrer le nouveau pointage
        Pointage nouveauPointage = createPointage(request, collaborateur);
        Pointage savedPointage = pointageRepository.save(nouveauPointage);

        // Journalisation de l'événement
        log.info("Pointage enregistré pour le collaborateur {} : {}",
                collaborateur.getNom(), savedPointage.getType());

        // Convertir et retourner le DTO
        return modelMapper.map(savedPointage, PointageDTO.class);
    }

    /**
     * Récupère le dernier pointage d'un collaborateur
     *
     * @param cin Numéro CIN du collaborateur
     * @return Dernier pointage ou null
     */
    @Transactional(readOnly = true)
    public PointageDTO getDernierPointage(String cin) {
        Collaborateur collaborateur = findCollaborateurByCin(cin);

        Optional<Pointage> lastPointageOptional = pointageRepository
                .findLastPointageByCollaborateur(collaborateur);

        return lastPointageOptional
                .map(pointage -> modelMapper.map(pointage, PointageDTO.class))
                .orElse(null);
    }

    /**
     * Génère un résumé des pointages pour un collaborateur
     *
     * @param cin Numéro CIN du collaborateur
     * @return Résumé des pointages
     */
    @Transactional(readOnly = true)
    public PointageResumeDTO getPointageResume(String cin) {
        Collaborateur collaborateur = findCollaborateurByCin(cin);

        // Définir la plage horaire du jour
        LocalDateTime debutJournee = LocalDate.now().atStartOfDay();
        LocalDateTime finJournee = LocalDate.now().atTime(LocalTime.MAX);

        // Récupérer les pointages de la journée
        List<Pointage> pointagesDuJour = pointageRepository
                .findByCollaborateurAndTimestampBetweenOrderByTimestampAsc(
                        collaborateur, debutJournee, finJournee
                );

        // Calculer les totaux
        long totalArrivees = pointageRepository.countByCollaborateurAndTypeAndTimestampBetween(
                collaborateur, PointageType.ARRIVEE, debutJournee, finJournee
        );
        long totalDeparts = pointageRepository.countByCollaborateurAndTypeAndTimestampBetween(
                collaborateur, PointageType.DEPART, debutJournee, finJournee
        );

        // Convertir les pointages en DTOs
        List<PointageDTO> pointageDTOs = pointagesDuJour.stream()
                .map(pointage -> modelMapper.map(pointage, PointageDTO.class))
                .collect(Collectors.toList());

        // Construire et retourner le résumé
        return PointageResumeDTO.builder()
                .totalArrivees(totalArrivees)
                .totalDeparts(totalDeparts)
                .pointages(pointageDTOs)
                .build();
    }

    // Méthodes privées de validation et de création

    /**
     * Valide les paramètres d'entrée
     */
    private void validateInputParameters(PointageRequest request, String cin) {
        if (request == null) {
            throw new IllegalArgumentException("La requête de pointage ne peut pas être nulle");
        }
        if (request.getType() == null) {
            throw new IllegalArgumentException("Le type de pointage est obligatoire");
        }
        if (cin == null || cin.trim().isEmpty()) {
            throw new IllegalArgumentException("Le CIN ne peut pas être vide");
        }
    }

    /**
     * Recherche un collaborateur par son CIN
     */
    private Collaborateur findCollaborateurByCin(String cin) {
        return collaborateurRepository.findByCin(cin)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Collaborateur non trouvé avec le CIN : " + cin
                ));
    }

    /**
     * Valide le nouveau pointage par rapport au dernier pointage
     */
    private void validateNewPointage(
            PointageRequest request,
            Optional<Pointage> lastPointageOptional
    ) {
        // Si aucun pointage précédent
        if (lastPointageOptional.isEmpty()) {
            // Le premier pointage doit être une arrivée
            if (request.getType() != PointageType.ARRIVEE) {
                throw new BusinessRuleException(
                        "Le premier pointage de la journée doit être une arrivée"
                );
            }
            return;
        }

        Pointage lastPointage = lastPointageOptional.get();
        LocalDate lastPointageDate = lastPointage.getTimestamp().toLocalDate();
        LocalDate today = LocalDate.now();

        // Si le dernier pointage n'est pas d'aujourd'hui
        if (!lastPointageDate.equals(today)) {
            // Réinitialiser la séquence pour une nouvelle journée
            if (request.getType() != PointageType.ARRIVEE) {
                throw new BusinessRuleException(
                        "Le premier pointage d'une nouvelle journée doit être une arrivée"
                );
            }
            return;
        }

        // Vérifier l'alternance des types de pointage
        if (lastPointage.getType() == request.getType()) {
            throw new BusinessRuleException(
                    "Impossible d'enregistrer deux pointages consécutifs du même type"
            );
        }

        // Vérifier l'ordre logique
        if (request.getType() == PointageType.DEPART &&
                lastPointage.getType() != PointageType.ARRIVEE) {
            throw new BusinessRuleException(
                    "Un départ doit être précédé d'une arrivée"
            );
        }
    }

    /**
     * Crée un nouveau pointage
     */
    private Pointage createPointage(
            PointageRequest request,
            Collaborateur collaborateur
    ) {
        return Pointage.builder()
                .type(request.getType())
                .timestamp(LocalDateTime.now())
                .collaborateur(collaborateur)
                .build();
    }
    public List<PointageDTO> getUserPointages(String cin, String month, Integer year) {
        // Récupérer le collaborateur
        Collaborateur collaborateur = findCollaborateurByCin(cin);

        // Définir la période de recherche
        LocalDateTime start, end;
        if (month != null && year != null) {
            YearMonth yearMonth = YearMonth.parse(year + "-" + month);
            start = yearMonth.atDay(1).atStartOfDay();
            end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        } else {
            // Par défaut, recherche pour le mois en cours
            LocalDate now = LocalDate.now();
            start = now.withDayOfMonth(1).atStartOfDay();
            end = now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX);
        }

        // Récupérer les pointages
        List<Pointage> pointages = pointageRepository
                .findByCollaborateurAndTimestampBetweenOrderByTimestampAsc(
                        collaborateur, start, end
                );

        // Convertir en DTOs
        return pointages.stream()
                .map(pointage -> modelMapper.map(pointage, PointageDTO.class))
                .collect(Collectors.toList());
    }
}