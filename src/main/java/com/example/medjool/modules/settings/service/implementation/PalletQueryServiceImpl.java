package com.example.medjool.modules.settings.service.implementation;

import com.example.medjool.modules.settings.service.PalletQueryService;
import com.example.medjool.modules.stock.model.Pallet;
import com.example.medjool.modules.stock.repository.PalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PalletQueryServiceImpl implements PalletQueryService {


    private final PalletRepository palletRepository;

    @Autowired
    public PalletQueryServiceImpl(PalletRepository palletRepository) {
        this.palletRepository = palletRepository;
    }

    @Override
    public List<Pallet> getAllPalletsByPackaging(float packaging) {
        return palletRepository.findAllByPackaging(
                packaging
        );
    }

    @Override
    public Pallet getPalletById(Integer id) {
        return palletRepository.findById(id).orElseThrow(null);
    }

    @Override
    public List<Pallet> getAllPallets() {
        return palletRepository.findAll();
    }
}
