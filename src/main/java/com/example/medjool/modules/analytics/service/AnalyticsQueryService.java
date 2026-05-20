package com.example.medjool.modules.analytics.service;

import com.example.medjool.modules.analytics.dto.OverviewDto;
import com.example.medjool.modules.order.dto.MarginClientResponseDto;

import java.util.List;

public interface AnalyticsQueryService {
    /**     * Retrieves an overview of the production orders, factory schedule, and stock.
     *
     * @return a JSON object containing the overview data.
     */
    OverviewDto getOverview();

    /** * Retrieves the margin for all clients for a specific product code.
     *
     * @param productCode the product code to filter by, or "all" for all products
     * @return a list of MarginClientResponseDto for each client
     */
    List<MarginClientResponseDto> getAllMarginPerClient(String productCode);
}
