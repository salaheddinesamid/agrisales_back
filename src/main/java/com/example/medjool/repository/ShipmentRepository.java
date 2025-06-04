package com.example.medjool.repository;

import com.example.medjool.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Shipment entities.
 * Provides methods to perform CRUD operations on Shipment data.
 */


public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}
