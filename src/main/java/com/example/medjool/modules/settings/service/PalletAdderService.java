package com.example.medjool.modules.settings.service;

import com.example.medjool.modules.settings.dto.PalletDto;
import com.example.medjool.modules.stock.model.Pallet;

public interface PalletAdderService {

    /**     * Adds a new pallet to the system.
     *
     * @param palletDto the data transfer object containing pallet details
     * @return a Pallet object saved in the database.
     */
    Pallet addPallet(PalletDto palletDto);
}
