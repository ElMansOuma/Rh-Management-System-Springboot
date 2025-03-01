package com.example.personnel_management.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class PieceJustificativeDTO {
    private Long id;
    private String type;
    private String nom;
    private String fichier;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateAjout;
    private Long collaborateurId;

    // Constructeurs
    public PieceJustificativeDTO() {}

    public PieceJustificativeDTO(Long id, String type, String nom, String fichier, Date dateAjout, Long collaborateurId) {
        this.id = id;
        this.type = type;
        this.nom = nom;
        this.fichier = fichier;
        this.dateAjout = dateAjout;
        this.collaborateurId = collaborateurId;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getFichier() {
        return fichier;
    }

    public void setFichier(String fichier) {
        this.fichier = fichier;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateAjout) {
        this.dateAjout = dateAjout;
    }

    public Long getCollaborateurId() {
        return collaborateurId;
    }

    public void setCollaborateurId(Long collaborateurId) {
        this.collaborateurId = collaborateurId;
    }
}
