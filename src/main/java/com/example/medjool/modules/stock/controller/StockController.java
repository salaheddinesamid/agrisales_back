package com.example.medjool.modules.stock.controller;

import com.example.medjool.modules.stock.dto.NewProductDto;
import com.example.medjool.modules.stock.dto.ProductResponseDto;
import com.example.medjool.modules.stock.service.implementation.StockAdderServiceImpl;
import com.example.medjool.modules.stock.service.implementation.StockInitializerServiceImpl;
import com.example.medjool.modules.stock.service.implementation.StockQueryServiceImpl;
import com.example.medjool.modules.stock.service.implementation.StockUpdateServiceImpl;
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

    private final StockQueryServiceImpl stockQueryService;
    private final StockAdderServiceImpl stockAdderService;
    private final StockUpdateServiceImpl stockUpdateService;
    private final StockInitializerServiceImpl stockInitializerService;

    @Autowired
    public StockController(StockQueryServiceImpl stockQueryService, StockAdderServiceImpl stockAdderService, StockUpdateServiceImpl stockUpdateService, StockInitializerServiceImpl stockInitializerService) {
        this.stockQueryService = stockQueryService;
        this.stockAdderService = stockAdderService;
        this.stockUpdateService = stockUpdateService;
        this.stockInitializerService = stockInitializerService;
    }

    /**     * Fetches all products from the stock.
     *
     * @return a ResponseEntity containing a list of ProductResponseDto with product details.
     */
    @GetMapping("get_all")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        List<ProductResponseDto> allProducts = stockQueryService.getAllProducts();
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    /**     * Creates a new product in the stock.
     *
     * @param newProductDto the DTO containing details of the new product.
     * @return a ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/new_product")
    public ResponseEntity<Object> createNewProduct(@RequestBody NewProductDto newProductDto) {
        try{
            ProductResponseDto response = new ProductResponseDto(
                    stockAdderService.addProduct(newProductDto)
            );
            return ResponseEntity.status(200)
                    .body(response);
        }catch(RuntimeException ex){
            return ResponseEntity.internalServerError().build();
        }
    }

    /**     * Fetches an overview of the stock.
     *
     * @return a ResponseEntity containing the stock overview.
     */
    /**
    @GetMapping("overview")
    public ResponseEntity<?> getStockOverview() {
        return overviewService.getOverview();
    }
    **/

    /**     * Updates the stock by processing a CSV file.
     *
     * @param file the CSV file containing product data.
     * @return a ResponseEntity indicating the result of the operation.
     * @throws IOException if an error occurs while reading the file.
     */
    @PutMapping("/update")
    public ResponseEntity<Object> updateStock(@RequestBody MultipartFile file ,
                                              @RequestParam(value = "week_number") Integer weekNumber) throws IOException {
        try{
            stockUpdateService.updateStock(file,weekNumber);
            return ResponseEntity   .status(200)
                    .body("The stock has been updated successfully");
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("An error occurred during stock update");
        }
    }

    @GetMapping("/product_code/get_all")
    public List<String> getAllProductCodes() {
        return List.of("");
    }

    /**     * Clears the stock by removing all products.
     *
     * @return a ResponseEntity indicating the result of the operation.
     */
    @PutMapping("/clear")
    public ResponseEntity<Object> clearStock() {
        try{
            stockUpdateService.clearStock();
            return ResponseEntity.status(200)
                    .body("The stock has been cleared successfully");
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("An error occurred during stock clearance");
        }

    }

    /**     * Initializes the stock by processing a CSV file.
     *
     * @param file the CSV file containing initial product data.
     * @return a ResponseEntity indicating the result of the operation.
     * @throws IOException if an error occurs while reading the file.
     */
    @PostMapping("/initialize")
    public ResponseEntity<Object> initializeStock(@RequestBody MultipartFile file) throws IOException {
        try{
            stockInitializerService.initializeStock(file);
            return ResponseEntity.status(200)
                    .body("The stock has been initialized successfully");
        }catch (Exception exception){
            return ResponseEntity.status(500)
                    .body("An error occurred during the stock initialization");
        }
    }
    
}
