package com.example.medjool.services;

import com.example.medjool.dto.FactoryScheduleResponseDto;
import com.example.medjool.dto.ProductionDetailsResponseDto;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductionService {

    void pushIntoProduction(Long orderId, LocalDateTime startDate) throws Exception;
    ResponseEntity<List<ProductionDetailsResponseDto>> getAllProductionDetails() throws Exception;
    ResponseEntity<List<FactoryScheduleResponseDto>> getFactorySchedule();
}
