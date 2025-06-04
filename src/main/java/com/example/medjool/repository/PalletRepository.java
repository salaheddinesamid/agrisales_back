package com.example.medjool.repository;

import com.example.medjool.model.Pallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing Pallet entities.
 * Provides methods to find pallets by packaging and pallet ID.
 */


public interface PalletRepository extends JpaRepository<Pallet, Integer> {

    List<Pallet> findAllByPackaging(float packaging);
    Pallet findByPackaging(float packaging);
    Pallet findByPalletId(int id);
}
