package com.example.medjool.repository;

import com.example.medjool.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Client entities.
 * Provides methods to perform CRUD operations and custom queries.
 */

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findByCompanyName(String name);
}
