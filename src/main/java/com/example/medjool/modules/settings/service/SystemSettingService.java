package com.example.medjool.modules.settings.service;

import com.example.medjool.modules.settings.dto.SettingDetailsDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SystemSettingService {

    ResponseEntity<?> updateMinProductLevel(double newMinProductLevel);
    ResponseEntity<List<SettingDetailsDto>> getAllSettings();
}
