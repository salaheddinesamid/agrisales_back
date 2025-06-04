package com.example.medjool.repository;

import com.example.medjool.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Contact entities.
 * Extends JpaRepository to provide CRUD operations.
 */
public interface ContactRepository extends JpaRepository<Contact, Integer> {
}
