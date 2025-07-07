package com.example.medjool.repository;

import com.example.medjool.model.Forex;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForexRepository extends JpaRepository<Forex, Long> {
}
