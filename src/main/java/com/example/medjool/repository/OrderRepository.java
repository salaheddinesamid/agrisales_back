package com.example.medjool.repository;

import com.example.medjool.model.Order;
import com.example.medjool.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** * Repository interface for managing Order entities.
 * Provides methods to perform CRUD operations and custom queries on Order data.
 */


public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByStatus(OrderStatus status);
}
