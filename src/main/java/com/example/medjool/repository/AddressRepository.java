package com.example.medjool.repository;

import com.example.medjool.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Address entities.
 * Extends JpaRepository to provide CRUD operations.
 */
public interface AddressRepository extends JpaRepository<Address, Long> {
}
