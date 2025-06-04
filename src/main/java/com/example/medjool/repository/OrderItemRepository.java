package com.example.medjool.repository;

import com.example.medjool.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing OrderItem entities.
 * Extends JpaRepository to provide CRUD operations.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
