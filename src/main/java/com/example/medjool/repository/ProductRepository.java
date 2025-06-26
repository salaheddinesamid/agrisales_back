package com.example.medjool.repository;

import com.example.medjool.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository interface for managing Product entities.
 * Provides methods to find products by various attributes.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByCallibreAndColorAndQualityAndFarm(String callibre, String color, String quality,String farm);
    Optional<Product> findByProductCode(String productCode);
    boolean existsByProductCode(String productCode);

    /**
     * Finds a product by its product code with a pessimistic write lock.
     * This method is used to ensure that the product is locked for updates
     * to prevent concurrent modifications.
     *
     * @param code the product code to search for
     * @return an Optional containing the Product if found, or empty if not found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productCode = :code")
    Optional<Product> findByProductCodeForUpdate(@Param("code") String code);

}
