package com.example.medjool.controller;

import com.example.medjool.dto.NewProductDto;
import com.example.medjool.dto.ProductResponseDto;
import com.example.medjool.services.implementation.OverviewServiceImpl;
import com.example.medjool.services.implementation.StockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


/** * Controller for managing stock-related operations such as fetching products, creating new products,
 * updating stock from a CSV file, clearing stock, and initializing stock.
 */


@RestController
@RequestMapping("api/stock/")
public class StockController {

    private final StockServiceImpl stockService;
    private final OverviewServiceImpl overviewService;

    @Autowired
    public StockController(StockServiceImpl stockService, OverviewServiceImpl overviewService) {
        this.stockService = stockService;
        this.overviewService = overviewService;
    }

    /**     * Fetches all products from the stock.
     *
     * @return a ResponseEntity containing a list of ProductResponseDto with product details.
     */
    @GetMapping("get_all")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        List<ProductResponseDto> allProducts = stockService.getAllProducts();
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    /**     * Creates a new product in the stock.
     *
     * @param newProductDto the DTO containing details of the new product.
     * @return a ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/new_product")
    public ResponseEntity<Object> createNewProduct(@RequestBody NewProductDto newProductDto) {
        return stockService.createNewProduct(newProductDto);
    }

    /**     * Fetches an overview of the stock.
     *
     * @return a ResponseEntity containing the stock overview.
     */
    @GetMapping("overview")
    public ResponseEntity<?> getStockOverview() {
        return overviewService.getOverview();
    }

    /**     * Updates the stock by processing a CSV file.
     *
     * @param file the CSV file containing product data.
     * @return a ResponseEntity indicating the result of the operation.
     * @throws IOException if an error occurs while reading the file.
     */
    @PutMapping("/update")
    public ResponseEntity<Object> updateStock(@RequestBody MultipartFile file) throws IOException {
        return stockService.updateStock(file);
    }

    /**     * Clears the stock by removing all products.
     *
     * @return a ResponseEntity indicating the result of the operation.
     */
    @PutMapping("/clear")
    public ResponseEntity<Object> clearStock() {
        return stockService.clearStock();
    }

    /**     * Initializes the stock by processing a CSV file.
     *
     * @param file the CSV file containing initial product data.
     * @return a ResponseEntity indicating the result of the operation.
     * @throws IOException if an error occurs while reading the file.
     */
    @PostMapping("/initialize")
    public ResponseEntity<Object> initializeStock(@RequestBody MultipartFile file) throws IOException {
        return stockService.initializeStock(file);
    }
    
}
