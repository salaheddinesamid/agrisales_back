package com.example.medjool.modules.settings.service;

import com.example.medjool.modules.stock.model.Pallet;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PalletQueryService {

    /**     * Retrieves all pallets by their packaging type.
     *
     * @param packaging the packaging type to filter pallets
     * @return a list of pallets matching the packaging type
     */
    List<Pallet> getAllPalletsByPackaging(float packaging);

    /**     * Retrieves a pallet by its ID.
     *
     * @param id the ID of the pallet to retrieve
     * @return the Pallet object with the specified ID
     */
    Pallet getPalletById(Integer id);

    /**     * Retrieves all pallets from the system.
     *
     * @return a ResponseEntity containing a list of all pallets
     */
    List<Pallet> getAllPallets();
}
