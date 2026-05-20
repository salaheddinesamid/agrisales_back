package com.example.medjool.modules.settings.controller;

import com.example.medjool.modules.settings.dto.SettingDetailsDto;
import com.example.medjool.modules.settings.service.implementation.SystemSettingServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@Slf4j
public class SystemSettingsController {

    private final SystemSettingServiceImpl systemSettingService;


    @Autowired
    public SystemSettingsController(SystemSettingServiceImpl systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    @PutMapping("/min_stock_level/update/")
    public ResponseEntity<?> updateMinStockLevel(@RequestParam double newMinStockLevel) throws Exception {
        return systemSettingService.updateMinProductLevel(newMinStockLevel);

    }

    @GetMapping("/get_all")
    public ResponseEntity<List<SettingDetailsDto>> getAllSystemSettings() {
        return systemSettingService.getAllSettings();
    }
}
