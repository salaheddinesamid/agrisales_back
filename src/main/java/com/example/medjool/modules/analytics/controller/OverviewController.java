package com.example.medjool.modules.analytics.controller;

import com.example.medjool.modules.analytics.service.implementation.AnalyticsQueryServiceImpl;
import com.example.medjool.modules.order.dto.MarginClientResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/margin_per_client")
public class OverviewController {

    private final AnalyticsQueryServiceImpl analyticsQueryService;

    @Autowired
    public OverviewController(AnalyticsQueryServiceImpl analyticsQueryService) {
        this.analyticsQueryService = analyticsQueryService;
    }

    /**     * Retrieves an overview of the production orders, factory schedule, and stock.
     *
     * @return ResponseEntity containing the overview data.
     */
    @GetMapping("/")
    public ResponseEntity<MarginClientResponseDto> getMarginPerClient(@RequestParam String companyName, @RequestParam String productCode) {
        return null;
    }

    /**     * Retrieves the margin for all clients for a specific product code.
     *
     * @param productCode the product code for which to retrieve the margin per client
     * @return ResponseEntity containing the margin data for all clients
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllMarginPerClient(@RequestParam String productCode) {
        try{
            return ResponseEntity.status(200)
                    .body(analyticsQueryService.getAllMarginPerClient(productCode));
        }catch (Exception exception){
            return ResponseEntity.status(200)
                    .body("An error occurred, please try again");
        }
    }
}
