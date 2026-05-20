package com.example.medjool.modules.stock.service;

import com.example.medjool.modules.stock.dto.NewProductDto;
import com.example.medjool.modules.stock.model.Product;

public interface StockAdderService {

    /** * Creates a new product in the stock.
     *
     * @param newProductDto the DTO containing details of the new product.
     * @return a ResponseEntity indicating the result of the operation.
     */
    Product addProduct(NewProductDto newProductDto);
}
