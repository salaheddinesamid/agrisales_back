package com.example.medjool.modules.stock.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StockInitializerService {
    /**     * Initializes the stock by reading product details from a CSV file.
     * The CSV should contain columns: product_code, Callibre, Color, Quality, Farm, Brand.
     *
     * @param file MultipartFile containing the CSV data.
     * @return ResponseEntity indicating success or failure of the operation.
     * @throws IOException if there is an error reading the file.
     */
    void initializeStock(MultipartFile file);
}
