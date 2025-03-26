package com.example.personnel_management.DTO;

import com.example.personnel_management.model.Pointage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointageRequest {
    private String cin;
    private Pointage.PointageType type;
}