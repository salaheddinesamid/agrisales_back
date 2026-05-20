package com.example.medjool.modules.stock.service.implementation;

import com.example.medjool.modules.stock.dto.NewProductDto;
import com.example.medjool.modules.stock.model.Product;
import com.example.medjool.modules.stock.repository.ProductRepository;
import com.example.medjool.modules.stock.service.StockAdderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StockAdderServiceImpl implements StockAdderService {

    private final ProductRepository productRepository;

    @Autowired
    public StockAdderServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product addProduct(NewProductDto newProductDto) {

        // Check if the product already exists
        Product product = productRepository.findByCallibreAndQualityAndFarm(
                newProductDto.getCallibre(),
                newProductDto.getQuality(),
                newProductDto.getFarm()
        );

        if (product != null) {
            throw new RuntimeException();
        } else {
            Product newProduct = new Product();
            newProduct.setCallibre(newProductDto.getCallibre());
            newProduct.setQuality(newProductDto.getQuality());
            newProduct.setFarm(newProductDto.getFarm());
            newProduct.setTotalWeight(newProductDto.getTotalWeight());

            return productRepository.save(newProduct);
        }
    }
}