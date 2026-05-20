package com.example.medjool.modules.user_management.service.implementation.repository;

import com.example.medjool.modules.production.model.FactorySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface FactoryScheduleRepository extends JpaRepository<FactorySchedule,Long> {
    FactorySchedule findByDate(LocalDate date);
}