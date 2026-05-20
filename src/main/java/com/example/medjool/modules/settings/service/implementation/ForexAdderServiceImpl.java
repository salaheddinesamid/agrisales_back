package com.example.medjool.modules.settings.service.implementation;

import com.example.medjool.modules.settings.dto.NewForexCurrencyDto;
import com.example.medjool.modules.settings.model.Forex;
import com.example.medjool.modules.settings.model.ForexCurrency;
import com.example.medjool.modules.settings.service.ForexAdderService;
import com.example.medjool.modules.user_management.service.implementation.repository.ForexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ForexAdderServiceImpl implements ForexAdderService {

    private final ForexRepository forexRepository;

    @Autowired
    public ForexAdderServiceImpl(ForexRepository forexRepository) {
        this.forexRepository = forexRepository;
    }

    @Override
    public Forex addForex(NewForexCurrencyDto forexDto) {
        try{
            boolean exists = forexRepository.existsByCurrency(ForexCurrency.valueOf(forexDto.getCurrencyName()));

            if(exists){
                throw new RuntimeException("Forex already exists");
            }
            else{
                Forex forex = new Forex();
                forex.setCurrency(ForexCurrency.valueOf(forexDto.getCurrencyName()));
                forex.setBuyingRate(forexDto.getBuyingRate());
                return forexRepository.save(forex);
            }
        }catch (RuntimeException exception){
            throw new RuntimeException();
        }
    }
}
