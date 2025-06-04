package com.example.medjool.repository;

import com.example.medjool.model.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing OrderHistory entities.
 * Provides methods to perform CRUD operations and custom queries.
 */


public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    OrderHistory findByOrderId(Long orderId);
}
