package com.example.medjool.repository;

import com.example.medjool.model.Forex;
import com.example.medjool.model.ForexCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForexRepository extends JpaRepository<Forex, Long> {

    boolean existsByCurrency(ForexCurrency currency);
    Optional<Forex> findByCurrency(ForexCurrency currency);
}
