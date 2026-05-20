package com.example.medjool.modules.settings.service.implementation;

import com.example.medjool.modules.settings.dto.UpdatePalletDto;
import com.example.medjool.modules.settings.helpers.PalletUpdateHelper;
import com.example.medjool.modules.settings.service.PalletUpdaterService;
import com.example.medjool.modules.stock.model.Pallet;
import com.example.medjool.modules.stock.repository.PalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PalletUpdaterServiceImpl implements PalletUpdaterService {

    private final PalletRepository palletRepository;
    private final PalletUpdateHelper palletUpdateHelper;

    @Autowired
    public PalletUpdaterServiceImpl(PalletRepository palletRepository, PalletUpdateHelper palletUpdateHelper) {
        this.palletRepository = palletRepository;
        this.palletUpdateHelper = palletUpdateHelper;
    }

    @Override
    @Transactional
    public Pallet updatePallet(Integer id, UpdatePalletDto palletDto) {
        Pallet pallet = palletRepository.findByPalletId(id);
        // Update dimensions:
        palletUpdateHelper.updatePalletDimensions(pallet,palletDto);
        // Update costs:
        palletUpdateHelper.updatePalletCosts(pallet,palletDto);
        // Update basic information:
        palletUpdateHelper.updatePalletBasicInformation(pallet,palletDto);

        return pallet;
    }

    @Override
    public void removePallet(Integer palletId) {
        Pallet pallet = palletRepository.findById(palletId).orElseThrow(null);
        palletRepository.delete(pallet);
    }
}
