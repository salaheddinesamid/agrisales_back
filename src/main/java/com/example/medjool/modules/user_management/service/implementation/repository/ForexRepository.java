package com.example.medjool.modules.user_management.service.implementation.repository;

import com.example.medjool.modules.settings.model.Forex;
import com.example.medjool.modules.settings.model.ForexCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForexRepository extends JpaRepository<Forex, Long> {

    boolean existsByCurrency(ForexCurrency currency);
    Optional<Forex> findByCurrency(ForexCurrency currency);
}
