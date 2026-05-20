package com.example.medjool.modules.settings.service.implementation;

import com.example.medjool.modules.settings.dto.SettingDetailsDto;
import com.example.medjool.modules.settings.model.SystemSetting;
import com.example.medjool.modules.settings.service.SystemSettingService;
import com.example.medjool.modules.user_management.service.implementation.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SystemSettingServiceImpl implements SystemSettingService {

    //private static int x = 0;
    private final SystemSettingRepository systemSettingRepository;

    @Autowired
    public SystemSettingServiceImpl(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    @Override
    public ResponseEntity<?> updateMinProductLevel(double newMinProductLevel) {

        Optional<SystemSetting> minProductLevel = systemSettingRepository.findByKey("min_stock_level");
        if (minProductLevel.isPresent()) {
            SystemSetting setting = minProductLevel.get();
            setting.setValue(newMinProductLevel);
            systemSettingRepository.save(setting);
            return ResponseEntity.ok("Minimum product level updated successfully");
        } else {
            return ResponseEntity.status(404).body("Setting not found");
        }
    }

    @Override
    public ResponseEntity<List<SettingDetailsDto>> getAllSettings(){
        List<SystemSetting> settings = systemSettingRepository.findAll();
        List<SettingDetailsDto> settingDetailsDtos = settings.stream()
                .map(setting -> new SettingDetailsDto(setting.getId(),setting.getKey(), setting.getValue()))
                .toList();
        return ResponseEntity.ok(settingDetailsDtos);
    }
}
