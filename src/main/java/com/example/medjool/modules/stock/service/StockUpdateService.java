package com.example.medjool.modules.stock.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StockUpdateService {


    /**     * Updates the stock of products based on the provided CSV file.
     * The CSV file should contain product codes and their corresponding total weights.
     *
     * @param file MultipartFile containing the CSV data.
     * @return ResponseEntity indicating success or failure of the operation.
     * @throws IOException if there is an error reading the file.
     */
    void updateStock(MultipartFile file, Integer weekNumber);

    /**     * Clears the stock by resetting the total weight of all products to 0.
     *
     * @return ResponseEntity indicating success of the operation.
     */
    void clearStock();
}
