package com.example.medjool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAnalyticsResponseDto {
    private int created;
    private int updated;
    private List<String> errors;
}
