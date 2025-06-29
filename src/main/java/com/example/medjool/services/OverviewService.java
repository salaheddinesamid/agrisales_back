package com.example.medjool.services;


import org.springframework.http.ResponseEntity;

public interface OverviewService {

    /**     * Retrieves an overview of the production orders, factory schedule, and stock.
     *
     * @return ResponseEntity containing the overview data.
     */
    ResponseEntity<?> getOverview();

    ResponseEntity<?> getMarginPerClient();
}
