package com.example.medjool.repository;

import com.example.medjool.model.ProductionOrder;
import com.example.medjool.model.ProductionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing ProductionOrder entities.
 * Provides methods to find production orders by their status.
 */

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder,Long> {
    List<ProductionOrder> findAllByProductionStatus(ProductionStatus productionStatus);
}
