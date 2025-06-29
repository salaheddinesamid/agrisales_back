package com.example.medjool.repository;

import com.example.medjool.model.Client;
import com.example.medjool.model.Order;
import com.example.medjool.model.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** * Repository interface for managing Order entities.
 * Provides methods to perform CRUD operations and custom queries on Order data.
 */


public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByStatus(OrderStatus status);
    List<Order> findAllByClient(Client client);

    /**     * Finds an Order by its ID and locks it for update to prevent concurrent modifications.
     *
     * @param id the ID of the Order to find
     * @return an Optional containing the found Order, or empty if not found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);
}
