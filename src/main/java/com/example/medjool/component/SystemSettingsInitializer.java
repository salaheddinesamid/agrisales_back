package com.example.medjool.component;

import com.example.medjool.modules.settings.model.SystemSetting;
import com.example.medjool.modules.user_management.service.implementation.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SystemSettingsInitializer {

    private final SystemSettingRepository systemSettingRepository;

    @Autowired
    public SystemSettingsInitializer(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        if(systemSettingRepository.findByKey("min_stock_level").isEmpty()){
            SystemSetting minStockLevel = new SystemSetting();
            minStockLevel.setKey("min_stock_level");
            minStockLevel.setValue(10.0);
            systemSettingRepository.save(minStockLevel);
        }
        if(systemSettingRepository.findByKey("factory_working_hours").isEmpty()){
            SystemSetting factoryWorkingHours = new SystemSetting();
            factoryWorkingHours.setKey("factory_working_hours");
            factoryWorkingHours.setValue(16.0);
            systemSettingRepository.save(factoryWorkingHours);
        }
    }
}
