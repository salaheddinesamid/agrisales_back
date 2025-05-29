package com.example.medjool.services.implementation;

import com.example.medjool.dto.NewProductDto;
import com.example.medjool.dto.ProductResponseDto;
import com.example.medjool.exception.ProductNotFoundException;
import com.example.medjool.model.Product;
import com.example.medjool.repository.ProductRepository;
import com.example.medjool.services.StockService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {

    private final ProductRepository productRepository;

    @Autowired
    public StockServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {

        System.out.println("👉 Fetching products from DB...");
        List<Product> products = productRepository.findAll();

        List<ProductResponseDto> productResponseDtos =
                products.stream().map(product -> new ProductResponseDto(product)).collect(Collectors.toList());
        return productResponseDtos;
    }

    // Upload a CSV file to update the stock
    @Override
    @Transactional
    public ResponseEntity<Object> updateStock(MultipartFile file) throws IOException {
        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        ) {
            for (CSVRecord record : csvParser) {
                System.out.println("Reading the row");
                String productCode = record.get("product_code");
                Double totalWeight = Double.parseDouble(record.get("total_weight"));
                Product product = productRepository.findByProductCode(productCode).orElseThrow(() -> new ProductNotFoundException());

                if (product != null) {
                    product.setTotalWeight(product.getTotalWeight() + totalWeight);
                } else {
                    // Optionally log or collect missing product IDs
                    System.out.println("Product not found: " + productCode);
                }
            }

            return new ResponseEntity<>("Stock updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update stock: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public ResponseEntity<Object> createNewProduct(NewProductDto newProductDto) {
        Product product = productRepository.findByCallibreAndColorAndQualityAndFarm(
                newProductDto.getCallibre(),
                newProductDto.getColor(),
                newProductDto.getQuality(),
                newProductDto.getFarm()
        );

        if (product != null) {
            return new ResponseEntity<>("Product already exists", HttpStatus.BAD_REQUEST);
        } else {
            Product newProduct = new Product();
            newProduct.setCallibre(newProductDto.getCallibre());
            newProduct.setColor(newProductDto.getColor());
            newProduct.setQuality(newProductDto.getQuality());
            newProduct.setFarm(newProductDto.getFarm());
            newProduct.setTotalWeight(newProductDto.getTotalWeight());

            productRepository.save(newProduct);
            return new ResponseEntity<>("New product created successfully", HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Object> clearStock() {
        productRepository.findAll().forEach(product -> {
            product.setTotalWeight(0.0); // Reset total weight to 0
            productRepository.save(product);
        });

        return new ResponseEntity<>("Stock cleared successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> initializeStock(MultipartFile file) throws IOException {
        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        ) {
            for (CSVRecord record : csvParser) {
                String productCode = safeTrim(record.get("product_code"));
                String callibre = safeTrim(record.get("Callibre"));
                String color = safeTrim(record.get("Color"));
                String quality = safeTrim(record.get("Quality"));
                String farm = safeTrim(record.get("Farm"));
                String brand = safeTrim(record.get("Brand"));

                if (productCode == null || callibre == null || color == null ||
                        quality == null || farm == null || brand == null) {
                    return new ResponseEntity<>("Invalid CSV format: Missing required columns or values", HttpStatus.BAD_REQUEST);
                }

                Product newProduct = new Product();
                newProduct.setProductCode(productCode);
                newProduct.setCallibre(callibre);
                newProduct.setColor(color);
                newProduct.setQuality(quality);
                newProduct.setFarm(farm);
                newProduct.setTotalWeight(0.0); // Initialized with 0 weight

                productRepository.save(newProduct);
            }

            return new ResponseEntity<>("Stock initialized successfully", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to process the file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Utility method to trim and return null if empty
    private String safeTrim(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}