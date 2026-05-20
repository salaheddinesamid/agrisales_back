package com.example.medjool.modules.stock.service.implementation;

import com.example.medjool.exception.ProductNotFoundException;
import com.example.medjool.modules.analytics.dto.UpdateAnalyticsRequestDto;
import com.example.medjool.modules.stock.model.Product;
import com.example.medjool.modules.stock.repository.ProductRepository;
import com.example.medjool.modules.stock.service.StockUpdateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
public class StockUpdateServiceImpl implements StockUpdateService {

    private final ProductRepository productRepository;
    private final CsvProcessorImpl csvProcessor;
    @Autowired
    public StockUpdateServiceImpl(ProductRepository productRepository, CsvProcessorImpl csvProcessor) {
        this.productRepository = productRepository;
        this.csvProcessor = csvProcessor;
    }

    public void updateAnalytics(List<UpdateAnalyticsRequestDto> requestDtoList, Integer weekNumber) {
        String url = "http://127.0.0.1:8000/stock/update/" + weekNumber + "/";

        try {
            // 1. Serialize the request body to JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(requestDtoList);

            System.out.println("📦 JSON Payload to Django:\n" + jsonPayload);

            // 2. Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 3. Build the HTTP request
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonPayload, headers);

            // 4. Configure RestTemplate with Jackson
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            // 5. Send the request
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            System.out.println("✅ Django Response: " + response.getStatusCode());
            System.out.println("📩 Response Body: " + response.getBody());

        } catch (HttpClientErrorException e) {
            System.err.println("❌ HTTP Error: " + e.getStatusCode());
            System.err.println("❌ Response Body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Django analytics update failed: " + e.getMessage(), e);

        } catch (Exception e) {
            System.err.println("❌ Unexpected error while sending analytics: " + e.getMessage());
            throw new RuntimeException("Analytics update failed", e);
        }
    }
    @Override
    @Transactional
    public void updateStock(MultipartFile file, Integer weekNumber) {
        List<UpdateAnalyticsRequestDto> updateAnalyticsRequestDto = new ArrayList<>();

        try {

            // Process and return csv data from the multipart file:
            CSVParser data = csvProcessor.processCSV(file);

            // Iterate over the CSV records to update each product:
            for (CSVRecord record : data) {
                try {
                    String productCode = record.get("product_code").trim();
                    Double totalWeight = Double.parseDouble(record.get("total_weight").trim());

                    // Check if product exists in DB
                    Product product = productRepository.findByProductCode(productCode)
                            .orElseThrow(ProductNotFoundException::new);

                    // Optionally update local DB weight if needed
                    product.setTotalWeight(product.getTotalWeight() + totalWeight);
                    productRepository.save(product);

                    UpdateAnalyticsRequestDto dto = new UpdateAnalyticsRequestDto(productCode,totalWeight);
                    updateAnalyticsRequestDto.add(dto);

                } catch (ProductNotFoundException | NumberFormatException e) {
                    // You can log these or store in a results object
                    System.err.println("Skipping record due to error: " + e.getMessage());
                }
            }

            // Send JSON data to Django analytics
            updateAnalytics(updateAnalyticsRequestDto, weekNumber);

        } catch (Exception e) {

        }
    }

    @Override
    public void clearStock() {
        productRepository.findAll().forEach(product -> {
            product.setTotalWeight(0.0); // Reset total weight to 0
            productRepository.save(product);
        });
    }
}
