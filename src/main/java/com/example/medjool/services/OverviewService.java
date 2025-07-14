package com.example.medjool.services;
import com.example.medjool.dto.MarginClientResponseDto;
import org.springframework.http.ResponseEntity;

public interface OverviewService {

    /**     * Retrieves an overview of the production orders, factory schedule, and stock.
     *
     * @return ResponseEntity containing the overview data.
     */
    ResponseEntity<?> getOverview();

    /**     * Retrieves the margin for a specific client based on company name and quality.
     *
     * @param companyName the name of the company for which to retrieve the margin
     * @param quality the quality level for which to retrieve the margin
     * @return ResponseEntity containing the margin data for the specified client
     */
    ResponseEntity<MarginClientResponseDto> getMarginPerClient(String companyName, String quality);

    /**     * Retrieves the margin for all clients for a specific product code.
     *
     * @param productCode the product code for which to retrieve the margin per client
     * @return ResponseEntity containing the margin data for all clients
     */
    ResponseEntity<?> getAllMarginPerClient(String productCode);
}
