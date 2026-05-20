package com.example.medjool.modules.settings.service;

import com.example.medjool.modules.settings.dto.UpdateForexDto;
import com.example.medjool.modules.settings.model.Forex;

public interface ForexUpdateService {

    /**     * Updates an existing Forex currency by ID.
     *
     * @param forexId the ID of the Forex currency to update
     * @param forexDto the DTO containing updated Forex details
     * @return an updated Forex object saved in the database.
     */
    Forex updateForex(Long forexId, UpdateForexDto forexDto);

}
