package com.example.medjool.modules.settings.service;

import com.example.medjool.modules.settings.dto.UpdatePalletDto;
import com.example.medjool.modules.stock.model.Pallet;

public interface PalletUpdaterService {

    /**     * Updates an existing pallet in the system.
     *
     * @param id the ID of the pallet to update
     * @param palletDto the data transfer object containing updated pallet details
     * @return an updated Pallet saved in the database.
     */
    Pallet updatePallet(Integer id, UpdatePalletDto palletDto);

    /**     * Deletes a pallet by ID.
     *
     * @param palletId the ID of the pallet to delete
     * @return ResponseEntity with a success message or an error message
     */
    void removePallet(Integer palletId);
}
