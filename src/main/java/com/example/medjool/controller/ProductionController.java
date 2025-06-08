package com.example.medjool.controller;

import com.example.medjool.dto.FactoryScheduleResponseDto;
import com.example.medjool.dto.ProductionDetailsResponseDto;
import com.example.medjool.services.implementation.ProductionServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/production")


public class ProductionController {

    private final ProductionServiceImpl productionService;

    public ProductionController(ProductionServiceImpl productionService) {
        this.productionService = productionService;
    }

    @GetMapping("/get_all")
    public ResponseEntity<List<ProductionDetailsResponseDto>> getAll() throws Exception {
        return productionService.getAllProductionDetails();
    }

    @GetMapping("factory/get_daily_schedule")
    public ResponseEntity<List<FactoryScheduleResponseDto>> getFactorySchedule() throws Exception {
        return productionService.getFactorySchedule();
    }
}
