package com.example.personnel_management.service;

import com.example.personnel_management.DTO.PieceJustificativeDTO;
import com.example.personnel_management.exception.ResourceNotFoundException;
import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.model.PieceJustificative;
import com.example.personnel_management.repository.CollaborateurRepository;
import com.example.personnel_management.repository.PieceJustificativeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PieceJustificativeService {
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final ModelMapper modelMapper;

    private final Path fileStoragePath = Paths.get("uploads").toAbsolutePath().normalize();

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(fileStoragePath);
    }

    // Récupérer toutes les pièces justificatives d'un collaborateur
    @Transactional(readOnly = true)
    public List<PieceJustificativeDTO> getPiecesByCollaborateur(Long collaborateurId) {
        List<PieceJustificative> pieces = pieceJustificativeRepository.findByCollaborateurId(collaborateurId);
        return pieces.stream()
                .map(piece -> modelMapper.map(piece, PieceJustificativeDTO.class))
                .collect(Collectors.toList());
    }

    // Validation de type de fichier
    private boolean isValidFileType(MultipartFile file) {
        // Définir les extensions de fichiers autorisées
        String[] allowedExtensions = {".pdf", ".jpg", ".jpeg", ".png", ".doc", ".docx", ".xls", ".xlsx"};

        // Récupérer le nom du fichier d'origine
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        // Vérifier si le fichier a une extension autorisée
        return Arrays.stream(allowedExtensions)
                .anyMatch(ext -> originalFilename.toLowerCase().endsWith(ext));
    }

    // Ajouter une pièce justificative
    @Transactional
    public PieceJustificativeDTO addPieceJustificative(Long collaborateurId, String type, MultipartFile file) throws IOException {
        // Valider le type de fichier
        if (!isValidFileType(file)) {
            throw new IllegalArgumentException("Type de fichier non autorisé");
        }

        // Vérifier la taille du fichier (5 Mo maximum)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("La taille du fichier ne doit pas dépasser 5 Mo");
        }

        Collaborateur collaborateur = collaborateurRepository.findById(collaborateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborateur non trouvé avec l'ID : " + collaborateurId));

        // Générer un nom de fichier unique
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Sauvegarder le fichier
        Path targetLocation = fileStoragePath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation);

        // Créer la pièce justificative
        PieceJustificative piece = PieceJustificative.builder()
                .nom(file.getOriginalFilename())
                .type(type)
                .fichierUrl("/uploads/" + fileName)
                .collaborateur(collaborateur)
                .build();

        PieceJustificative savedPiece = pieceJustificativeRepository.save(piece);
        return modelMapper.map(savedPiece, PieceJustificativeDTO.class);
    }

    // Supprimer une pièce justificative
    @Transactional
    public void deletePieceJustificative(Long id) throws IOException {
        PieceJustificative piece = pieceJustificativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pièce justificative non trouvée avec l'ID : " + id));

        // Supprimer le fichier
        String fileName = piece.getFichierUrl().substring(piece.getFichierUrl().lastIndexOf("/") + 1);
        Path filePath = fileStoragePath.resolve(fileName);
        Files.deleteIfExists(filePath);

        // Supprimer l'entité de la base de données
        pieceJustificativeRepository.delete(piece);
    }
    @Transactional
    public PieceJustificativeDTO updatePieceJustificative(Long id, String type, MultipartFile file) throws IOException {
        // Valider le type de fichier
        if (!isValidFileType(file)) {
            throw new IllegalArgumentException("Type de fichier non autorisé");
        }

        // Vérifier la taille du fichier (5 Mo maximum)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("La taille du fichier ne doit pas dépasser 5 Mo");
        }

        // Récupérer la pièce justificative existante
        PieceJustificative piece = pieceJustificativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pièce justificative non trouvée avec l'ID : " + id));

        // Supprimer l'ancien fichier
        String oldFileName = piece.getFichierUrl().substring(piece.getFichierUrl().lastIndexOf("/") + 1);
        Path oldFilePath = fileStoragePath.resolve(oldFileName);
        Files.deleteIfExists(oldFilePath);

        // Générer un nouveau nom de fichier
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Sauvegarder le nouveau fichier
        Path targetLocation = fileStoragePath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation);

        // Mettre à jour la pièce justificative
        piece.setNom(file.getOriginalFilename());
        piece.setType(type);
        piece.setFichierUrl("/uploads/" + fileName);

        PieceJustificative updatedPiece = pieceJustificativeRepository.save(piece);
        return modelMapper.map(updatedPiece, PieceJustificativeDTO.class);
    }
}
