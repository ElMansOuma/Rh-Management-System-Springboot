package com.example.personnel_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointageResumeDTO {
    private long totalArrivees;
    private long totalDeparts;
    private List<PointageDTO> pointages;
}