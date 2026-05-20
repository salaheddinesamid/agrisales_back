package com.example.medjool.modules.settings.service;

import com.example.medjool.modules.settings.dto.NewForexCurrencyDto;
import com.example.medjool.modules.settings.model.Forex;

public interface ForexAdderService {
    /**     * Adds a new Forex currency to the system.
     *
     * @param forexDto the DTO containing Forex currency details
     * @return Forex object saved in the database.
     */
    Forex addForex(NewForexCurrencyDto forexDto);

}
