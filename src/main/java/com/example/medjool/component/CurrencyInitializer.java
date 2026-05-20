package com.example.medjool.component;

import com.example.medjool.modules.settings.model.Forex;
import com.example.medjool.modules.settings.model.ForexCurrency;
import com.example.medjool.modules.user_management.service.implementation.repository.ForexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CurrencyInitializer {

    private final ForexRepository forexRepository;

    private String[] currencies = {
        "USD", "EUR"
    };
    @Autowired
    public CurrencyInitializer(ForexRepository forexRepository) {
        this.forexRepository = forexRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCurrencies(){
        for(String currency : currencies) {
            if (!forexRepository.existsByCurrency(ForexCurrency.valueOf(currency))) {
                Forex forex = new Forex();
                forex.setCurrency(ForexCurrency.valueOf(currency));
                forex.setBuyingRate(0.0); // Default buying rate
                forexRepository.save(forex); // Assuming 1.0 is the default rate
            }
        }
    }
}
