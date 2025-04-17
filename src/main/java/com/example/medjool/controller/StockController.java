package com.example.medjool.controller;

import com.example.medjool.dto.OverviewDto;
import com.example.medjool.dto.ProductResponseDto;
import com.example.medjool.model.Product;
import com.example.medjool.services.OverviewService;
import com.example.medjool.services.StockService;
import com.example.medjool.services.implementation.OverviewServiceImpl;
import com.example.medjool.services.implementation.StockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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


    @GetMapping("get_all")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        List<ProductResponseDto> allProducts = stockService.getAllProducts();
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }


    @GetMapping("overview")
    public ResponseEntity<OverviewDto> getStockOverview() {
        return overviewService.getOverview();
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateStock(@RequestBody MultipartFile file) throws IOException {
        return stockService.updateStock(file);
    }

    
}
