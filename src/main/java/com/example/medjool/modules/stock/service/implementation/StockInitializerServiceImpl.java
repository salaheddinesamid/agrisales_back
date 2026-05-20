package com.example.medjool.modules.stock.service.implementation;

import com.example.medjool.modules.stock.model.Product;
import com.example.medjool.modules.stock.repository.ProductRepository;
import com.example.medjool.modules.stock.service.StockInitializerService;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StockInitializerServiceImpl implements StockInitializerService {

    private final ProductRepository productRepository;
    private final CsvProcessorImpl csvProcessor;

    @Autowired
    public StockInitializerServiceImpl(ProductRepository productRepository, CsvProcessorImpl csvProcessor) {
        this.productRepository = productRepository;
        this.csvProcessor = csvProcessor;
    }

    @Override
    public void initializeStock(MultipartFile file) {
        try {

            // Parse the CSV file and extract the records:
            CSVParser csvRecords = csvProcessor.processCSV(file);

            for (CSVRecord record : csvRecords) {
                String productCode = safeTrim(record.get("product_code"));
                String callibre = safeTrim(record.get("Callibre"));
                String color = safeTrim(record.get("Color"));
                String quality = safeTrim(record.get("Quality"));
                String farm = safeTrim(record.get("Farm"));
                String brand = safeTrim(record.get("Brand"));

                if (productCode == null || callibre == null || color == null ||
                        quality == null || farm == null || brand == null) {
                    throw new RuntimeException("Invalid CSV format: Missing required columns or values");
                }

                Product newProduct = new Product();
                newProduct.setProductCode(productCode);
                newProduct.setCallibre(callibre);
                newProduct.setQuality(quality);
                newProduct.setFarm(farm);
                newProduct.setTotalWeight(0.0); // Initialized with 0 weight

                productRepository.save(newProduct);
            }
        }catch (RuntimeException ex){
        }
    }

    // Utility method to trim and return null if empty
    private String safeTrim(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}