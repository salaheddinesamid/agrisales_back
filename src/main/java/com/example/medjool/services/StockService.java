package com.example.medjool.services;

import com.example.medjool.dto.NewProductDto;
import com.example.medjool.dto.ProductResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StockService {

    List<ProductResponseDto> getAllProducts();
    ResponseEntity<Object> updateStock(MultipartFile file) throws IOException;
    ResponseEntity<Object> createNewProduct(NewProductDto newProductDto);
    ResponseEntity<Object> clearStock();
    ResponseEntity<Object> initializeStock(MultipartFile file) throws IOException;

}
