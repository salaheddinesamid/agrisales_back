package com.example.medjool.services.implementation;

import com.example.medjool.dto.NewProductDto;
import com.example.medjool.dto.ProductResponseDto;
import com.example.medjool.dto.UpdateAnalyticsRequestDto;
import com.example.medjool.exception.ProductNotFoundException;
import com.example.medjool.model.Product;
import com.example.medjool.repository.ProductRepository;
import com.example.medjool.services.StockService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StockServiceImpl implements StockService {

    private final ProductRepository productRepository;
    //private final RestTemplate restTemplate;

    @Autowired
    public StockServiceImpl(ProductRepository productRepository, RestTemplate restTemplate) {
        this.productRepository = productRepository;
        //this.restTemplate = restTemplate;
    }

    /**     * Fetches all products from the database and returns them as a list of ProductResponseDto.
     *
     * @return List of ProductResponseDto containing product details.
     */
    @Override
    public List<ProductResponseDto> getAllProducts() {

        System.out.println("👉 Fetching products from DB...");
        List<Product> products = productRepository.findAll();

        return products.stream().map(ProductResponseDto::new).collect(Collectors.toList());
    }

    /**     * Updates the stock of products based on the provided CSV file.
     * The CSV file should contain product codes and their corresponding total weights.
     *
     * @param file MultipartFile containing the CSV data.
     * @return ResponseEntity indicating success or failure of the operation.
     * @throws IOException if there is an error reading the file.
     */
    @Override
    @Transactional
    public ResponseEntity<Object> updateStock(MultipartFile file, Integer weekNumber) {
        List<UpdateAnalyticsRequestDto> updateAnalyticsRequestDto = new ArrayList<>();

        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        ) {
            for (CSVRecord record : csvParser) {
                try {
                    String productCode = record.get("product_code").trim();
                    Double totalWeight = Double.parseDouble(record.get("total_weight").trim());

                    // Check if product exists in DB
                    Product product = productRepository.findByProductCode(productCode)
                            .orElseThrow(() -> new ProductNotFoundException());

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

            return new ResponseEntity<>("Stock updated successfully", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update stock: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /*
    private void updateAnalytics(List<UpdateAnalyticsRequestDto> requestDtoList, Integer weekNumber) {
        String url = "http://127.0.0.1:8000/stock/update/" + weekNumber + "/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<UpdateAnalyticsRequestDto>> httpEntity = new HttpEntity<>(requestDtoList, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );
            log.info("The request body sent to the analytics service is : " + requestDtoList.toString());
            System.out.println("Analytics update response: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Failed to send analytics data: " + e.getMessage());
            log.info("The request body is: " + requestDtoList.toString() );
            throw new RuntimeException("Analytics update failed", e);
        }
    }

     */

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





    /**     * Creates a new product in the stock based on the provided NewProductDto.
     * Checks if a product with the same callibre, color, quality, and farm already exists.
     *
     * @param newProductDto DTO containing details of the new product to be created.
     * @return ResponseEntity indicating success or failure of the operation.
     */
    @Override
    public ResponseEntity<Object> createNewProduct(NewProductDto newProductDto) {
        Product product = productRepository.findByCallibreAndQualityAndFarm(
                newProductDto.getCallibre(),
                newProductDto.getQuality(),
                newProductDto.getFarm()
        );

        if (product != null) {
            return new ResponseEntity<>("Product already exists", HttpStatus.BAD_REQUEST);
        } else {
            Product newProduct = new Product();
            newProduct.setCallibre(newProductDto.getCallibre());
            newProduct.setQuality(newProductDto.getQuality());
            newProduct.setFarm(newProductDto.getFarm());
            newProduct.setTotalWeight(newProductDto.getTotalWeight());

            productRepository.save(newProduct);
            return new ResponseEntity<>("New product created successfully", HttpStatus.OK);
        }
    }

    /**     * Clears the stock by resetting the total weight of all products to 0.
     *
     * @return ResponseEntity indicating success of the operation.
     */
    @Override
    public ResponseEntity<Object> clearStock() {
        productRepository.findAll().forEach(product -> {
            product.setTotalWeight(0.0); // Reset total weight to 0
            productRepository.save(product);
        });

        return new ResponseEntity<>("Stock cleared successfully", HttpStatus.OK);
    }


    /**     * Initializes the stock by reading product details from a CSV file.
     * The CSV should contain columns: product_code, Callibre, Color, Quality, Farm, Brand.
     *
     * @param file MultipartFile containing the CSV data.
     * @return ResponseEntity indicating success or failure of the operation.
     * @throws IOException if there is an error reading the file.
     */
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

    @Override
    @Cacheable(value = "productCodes", key = "'allProductCodes'")
    public List<String> getAllProductCode() {
        return productRepository.findAll()
                .stream().map(Product::getProductCode)
                .collect(Collectors.toList());
    }

    // Utility method to trim and return null if empty
    private String safeTrim(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }



}

class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() {
        return -1; // unknown length - forces multipart boundary to not include it
    }
}
