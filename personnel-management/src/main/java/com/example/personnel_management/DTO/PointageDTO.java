package com.example.personnel_management.DTO;

import com.example.personnel_management.model.Pointage.PointageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointageDTO {
    private Long id;
    private PointageType type;
    private LocalDateTime timestamp;
}