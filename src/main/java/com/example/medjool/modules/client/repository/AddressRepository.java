package com.example.medjool.modules.client.repository;

import com.example.medjool.modules.client.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Address entities.
 * Extends JpaRepository to provide CRUD operations.
 */
public interface AddressRepository extends JpaRepository<Address, Long> {
}
