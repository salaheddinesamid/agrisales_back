package com.example.medjool.component;
import com.example.medjool.dto.NewProductDto;
import com.example.medjool.model.Product;
import com.example.medjool.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/** * StockInitializer is a component that initializes the stock of products
 * when the application is ready. It checks if each product already exists
 * in the database and only adds it if it does not.
 */


@Component
public class StockInitializer {

    private final ProductRepository productRepository;


    @Autowired
    public StockInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;

    }

    private final List<NewProductDto> products = List.of(
            new NewProductDto("S00_EA0_D_MS", "Small", "Export A",  "Medjool",0),
            new NewProductDto("S00_EB0_D_MS", "Small", "Export B",  "Medjool", 0),
            new NewProductDto("S00_EC0_D_MS", "Small", "Export C",  "Medjool",0),
            new NewProductDto("S00_GMS_D_MS", "Small", "GMS",  "Medjool",0),
            new NewProductDto("S00_ML0_D_MS", "Small", "ML",  "Medjool",0),
            new NewProductDto("M00_EA0_D_MS", "Medium", "Export A",  "Medjool",0),
            new NewProductDto("M00_EB0_D_MS", "Medium", "Export B",  "Medjool",0),
            new NewProductDto("M00_EC0_D_MS", "Medium", "Export C",  "Medjool",0),
            new NewProductDto("M00_GMS_D_MS", "Medium", "GMS",  "Medjool",0),
            new NewProductDto("M00_ML0_D_MS", "Medium", "ML",  "Medjool", 0),
            new NewProductDto("L00_EA0_D_MS", "Large", "Export A",  "Medjool", 0),
            new NewProductDto("L00_EB0_D_MS", "Large", "Export B",  "Medjool",0),
            new NewProductDto("L00_EC0_D_MS", "Large", "Export C",  "Medjool", 0),
            new NewProductDto("L00_GMS_D_MS", "Large", "GMS",  "Medjool",0),
            new NewProductDto("L00_ML0_D_MS", "Large", "ML",  "Medjool",0),
            new NewProductDto("J00_EA0_D_MS", "Jumbo", "Export A",  "Medjool",0),
            new NewProductDto("J00_EB0_D_MS", "Jumbo", "Export B",  "Medjool",0),
            new NewProductDto("J00_EC0_D_MS", "Jumbo", "Export C",  "Medjool",0),
            new NewProductDto("J00_GMS_D_MS", "Jumbo", "GMS",  "Medjool",0),
            new NewProductDto("J00_ML0_D_MS", "Jumbo", "ML",  "Medjool",0),
            new NewProductDto("SJ0_EA0_D_MS", "Super Jumbo", "Export A",  "Medjool",0),
            new NewProductDto("SJ0_EB0_D_MS", "Super Jumbo", "Export B",  "Medjool", 0),
            new NewProductDto("SJ0_EC0_D_MS", "Super Jumbo", "Export C",  "Medjool", 0),
            new NewProductDto("SJ0_GMS_D_MS", "Super Jumbo", "GMS",  "Medjool",0),
            new NewProductDto("SJ0_ML0_D_MS", "Super Jumbo", "ML",  "Medjool", 0),
            new NewProductDto("S00_EA0_D_MS", "Small", "Export A",  "Medjool",0),
            new NewProductDto("S00_EB0_D_MS", "Small", "Export B",  "Medjool",0),
            new NewProductDto("S00_EC0_D_MS", "Small", "Export C",  "Medjool",0),
            new NewProductDto("S00_GMS_D_MS", "Small", "GMS",  "Medjool",0),
            new NewProductDto("S00_ML0_D_MS", "Small", "ML",  "Medjool",0),
            new NewProductDto("M00_EA0_D_HN", "Medium", "Export A",  "Hanich",0),
            new NewProductDto("M00_EB0_D_HN", "Medium", "Export B",  "Hanich",0),
            new NewProductDto("M00_EC0_D_HN", "Medium", "Export C",  "Hanich",0),
            new NewProductDto("M00_GMS_D_HN", "Medium", "GMS",  "Hanich", 0),
            new NewProductDto("M00_ML0_D_HN", "Medium", "ML",  "Hanich",0),
            new NewProductDto("BBS_EA0_D_MS", "BBS", "Export A",  "Medjool", 0),
            new NewProductDto("BBS_EB0_D_MS", "BBS", "Export B",  "Medjool", 0),
            new NewProductDto("BBS_EC0_D_MS", "BBS", "Export C",  "Medjool",0),
            new NewProductDto("BBS_GMS_D_MS", "BBS", "GMS",  "Medjool",0),
            new NewProductDto("BBS_ML_D_MS", "BBS", "ML",  "Medjool",0)
    );

    /**
     * This method is called when the application is ready.
     * It initializes the stock by checking if each product exists in the database.
     * If a product does not exist, it creates a new Product entity and saves it to the repository.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        products.forEach(productDto -> {
            try {
                if(productRepository.existsByProductCode(productDto.getProductCode())){
                    System.out.println("Product already exists: " + productDto.getProductCode());
                } else {
                    Product product = new Product();
                    product.setProductCode(productDto.getProductCode());
                    product.setCallibre(productDto.getCallibre());
                    product.setFarm(productDto.getFarm());
                    product.setQuality(productDto.getQuality());
                    product.setTotalWeight(productDto.getTotalWeight());

                    productRepository.save(product); // Call the service to persist
                }
            } catch (Exception e) {
                System.out.println("Product already exists: " + productDto.getProductCode());
            }
        });
    }

}
