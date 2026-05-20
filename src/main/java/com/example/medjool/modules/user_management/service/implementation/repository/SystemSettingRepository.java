package com.example.medjool.modules.user_management.service.implementation.repository;

import com.example.medjool.modules.settings.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    Optional<SystemSetting> findByKey(String key);
}
