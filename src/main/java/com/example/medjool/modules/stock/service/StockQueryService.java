package com.example.medjool.modules.stock.service;

import com.example.medjool.modules.stock.dto.ProductResponseDto;

import java.util.List;

public interface StockQueryService {

    /** * Fetches all products from the stock.
     *
     * @return a list of ProductResponseDto containing product details.
     */
    List<ProductResponseDto> getAllProducts();

    boolean validateStock(String productCode, double itemWeight);
}
