package com.example.personnel_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PieceJustificativeRequest {
    private MultipartFile file;
    private String description;
    private String type;
    private Long collaborateurId;
}