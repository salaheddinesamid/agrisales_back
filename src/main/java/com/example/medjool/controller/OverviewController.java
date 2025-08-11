package com.example.medjool.controller;

import com.example.medjool.dto.MarginClientResponseDto;
import com.example.medjool.services.implementation.OverviewServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/margin_per_client")
public class OverviewController {

    private final OverviewServiceImpl overviewService;

    @Autowired
    public OverviewController(OverviewServiceImpl overviewService) {
        this.overviewService = overviewService;
    }

    /**     * Retrieves an overview of the production orders, factory schedule, and stock.
     *
     * @return ResponseEntity containing the overview data.
     */
    @GetMapping("/")
    public ResponseEntity<MarginClientResponseDto> getMarginPerClient(@RequestParam String companyName, @RequestParam String productCode) {
        return overviewService.getMarginPerClient(companyName,productCode);
    }

    /**     * Retrieves the margin for all clients for a specific product code.
     *
     * @param productCode the product code for which to retrieve the margin per client
     * @return ResponseEntity containing the margin data for all clients
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllMarginPerClient(@RequestParam String productCode) {
        return overviewService.getAllMarginPerClient(productCode);
    }
}
