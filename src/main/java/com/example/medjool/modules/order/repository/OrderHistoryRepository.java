package com.example.medjool.modules.order.repository;

import com.example.medjool.modules.order.model.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing OrderHistory entities.
 * Provides methods to perform CRUD operations and custom queries.
 */


public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    OrderHistory findByOrderId(Long orderId);
}
