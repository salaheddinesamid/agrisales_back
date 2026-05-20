package com.example.medjool.modules.settings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class SettingDetailsDto {
    private long id;
    private String key;
    private double  value;

}
