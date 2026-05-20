package com.example.medjool.modules.order.repository;

import com.example.medjool.modules.order.model.OrderItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository interface for managing OrderItem entities.
 * Extends JpaRepository to provide CRUD operations.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT oi FROM OrderItem oi WHERE oi.id = :id")
    Optional<OrderItem> findByIdForUpdate(@Param("id") Long id);
}
