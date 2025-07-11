package com.example.medjool.services;

import com.example.medjool.dto.NewProductDto;
import com.example.medjool.dto.ProductResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StockService {

    /** * Fetches all products from the stock.
     *
     * @return a list of ProductResponseDto containing product details.
     */
    List<ProductResponseDto> getAllProducts();

    /** * Updates the stock by processing a CSV file.
     *
     * @param file the CSV file containing product data.
     * @return a ResponseEntity indicating the result of the operation.
     * @throws IOException if an error occurs while reading the file.
     */
    ResponseEntity<Object> updateStock(MultipartFile file) throws IOException;

    /** * Creates a new product in the stock.
     *
     * @param newProductDto the DTO containing details of the new product.
     * @return a ResponseEntity indicating the result of the operation.
     */
    ResponseEntity<Object> createNewProduct(NewProductDto newProductDto);

    /** * clear the stock.
     *
     * @return a ResponseEntity indicating the result of the operation.
     */
    ResponseEntity<Object> clearStock();

    /** * Initializes the stock by processing a CSV file.
     *
     * @param file the CSV file containing initial product data.
     * @return a ResponseEntity indicating the result of the operation.
     * @throws IOException if an error occurs while reading the file.
     */
    ResponseEntity<Object> initializeStock(MultipartFile file) throws IOException;

}
