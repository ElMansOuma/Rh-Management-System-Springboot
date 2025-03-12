package com.example.personnel_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceJustificativeDTO {
    private Long id;
    private String nom;
    private String type;
    private String fichierUrl;

}