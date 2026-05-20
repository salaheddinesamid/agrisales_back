package com.example.medjool.modules.settings.service.implementation;

import com.example.medjool.modules.settings.dto.UpdateForexDto;
import com.example.medjool.modules.settings.model.Forex;
import com.example.medjool.modules.settings.service.ForexUpdateService;
import org.springframework.stereotype.Service;

@Service
public class ForexUpdaterServiceImpl implements ForexUpdateService {
    @Override
    public Forex updateForex(Long forexId, UpdateForexDto forexDto) {
        return null;
    }
}
