package com.example.medjool.controller;

import com.example.medjool.dto.MarginClientResponseDto;
import com.example.medjool.dto.OrderCostDto;
import com.example.medjool.services.implementation.OverviewServiceImpl;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/margin_per_client")
public class OverviewController {

    private final OverviewServiceImpl overviewService;

    @Autowired
    public OverviewController(OverviewServiceImpl overviewService) {
        this.overviewService = overviewService;
    }

    @GetMapping("/")
    public ResponseEntity<MarginClientResponseDto> getMarginPerClient(@RequestParam String companyName, @RequestParam String productCode) {
        return overviewService.getMarginPerClient(companyName,productCode);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMarginPerClient(@RequestParam String productCode) {
        return overviewService.getAllMarginPerClient(productCode);
    }
}
