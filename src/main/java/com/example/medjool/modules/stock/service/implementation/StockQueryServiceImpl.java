package com.example.medjool.modules.stock.service.implementation;

import com.example.medjool.modules.stock.dto.ProductResponseDto;
import com.example.medjool.modules.stock.model.Product;
import com.example.medjool.modules.stock.repository.ProductRepository;
import com.example.medjool.modules.stock.service.StockQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class StockQueryServiceImpl implements StockQueryService {

    private final ProductRepository productRepository;

    @Autowired
    public StockQueryServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        System.out.println("👉 Fetching products from DB...");
        List<Product> products = productRepository.findAll();

        return products.stream().map(ProductResponseDto::new).collect(Collectors.toList());
    }

    @Override
    public boolean validateStock(String productCode, double itemWeight) {
        // Fetch the product from the database:
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow();

        // Check if the stock has enough weight
        return product.getTotalWeight() <= itemWeight;
    }
}
