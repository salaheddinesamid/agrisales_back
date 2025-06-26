package com.example.medjool.repository;

import com.example.medjool.model.Client;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository interface for managing Client entities.
 * Provides methods to perform CRUD operations and custom queries.
 */

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findByCompanyName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Client c WHERE c.companyName = :companyName")
    Optional<Client> findByCompanyNameForUpdate(@Param("companyName") String companyName);
}
