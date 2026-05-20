package com.example.medjool.modules.stock.controller;

import com.example.medjool.modules.stock.dto.NewProductDto;
import com.example.medjool.modules.stock.dto.ProductResponseDto;
import com.example.medjool.modules.stock.service.implementation.StockAdderServiceImpl;
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
    private final OverviewServiceImpl overviewService;

    @Autowired
    public StockController(StockQueryServiceImpl stockQueryService, StockAdderServiceImpl stockAdderService, StockUpdateServiceImpl stockUpdateService, OverviewServiceImpl overviewService) {
        this.stockQueryService = stockQueryService;
        this.stockAdderService = stockAdderService;
        this.stockUpdateService = stockUpdateService;
        this.overviewService = overviewService;
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
    public ResponseEntity<Object> updateStock(@RequestBody MultipartFile file ,
                                              @RequestParam(value = "week_number") Integer weekNumber) throws IOException {
        return stockService.updateStock(file,weekNumber);
    }

    @GetMapping("/product_code/get_all")
    public List<String> getAllProductCodes() {
        return stockService.getAllProductCode();
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
