package com.example.medjool.modules.settings.service.implementation;

import com.example.medjool.modules.settings.dto.PalletDto;
import com.example.medjool.modules.settings.helpers.PalletAdderHelper;
import com.example.medjool.modules.settings.service.PalletAdderService;
import com.example.medjool.modules.stock.model.Pallet;
import com.example.medjool.modules.stock.repository.PalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PalletAdderServiceImpl implements PalletAdderService {

    private final PalletRepository palletRepository;
    private final PalletAdderHelper palletAdderHelper;

    @Autowired
    public PalletAdderServiceImpl(PalletRepository palletRepository, PalletAdderHelper palletAdderHelper) {
        this.palletRepository = palletRepository;
        this.palletAdderHelper = palletAdderHelper;
    }

    @Override
    public Pallet addPallet(PalletDto palletDto) {
        Pallet pallet = palletRepository.findByPackaging(palletDto.getPackaging());

        if(pallet != null){
            throw  new RuntimeException("Pallet already exists");
        }
        Pallet newPallet = new Pallet();

        int totalBoxes = 0;
        float totalWeight = 0;

        if(palletDto.getPackaging() == 5){
            newPallet.setPackaging(5);
            newPallet.setNumberOfBoxesInStory(palletDto.getNumberOfBoxesInStory());
            newPallet.setNumberOfStoriesInPallet(palletDto.getNumberOfStoriesInPallet());
            totalBoxes = newPallet.getNumberOfBoxesInStory() * newPallet.getNumberOfStoriesInPallet();
            totalWeight = totalBoxes * 5;
            newPallet.setTotalNet(totalWeight);
            newPallet.setNumberOfBoxesInPallet(totalBoxes);
        }else{
            newPallet.setPackaging(palletDto.getPackaging());
            newPallet.setNumberOfBoxesInCarton(palletDto.getNumberOfBoxesInCarton());
            newPallet.setNumberOfCartonsInStory(palletDto.getNumberOfCartonsInStory());
            newPallet.setNumberOfStoriesInPallet(palletDto.getNumberOfStoriesInPallet());

            totalBoxes = newPallet.getNumberOfBoxesInCarton() * newPallet.getNumberOfCartonsInStory() * newPallet.getNumberOfStoriesInPallet();
            totalWeight = totalBoxes * newPallet.getPackaging();
            newPallet.setNumberOfBoxesInPallet(totalBoxes);
            newPallet.setTotalNet(totalWeight);
        }

        // Dimensions:
        palletAdderHelper.addPalletDimensions(newPallet, palletDto);

        // Costs:
        palletAdderHelper.addPalletCosts(newPallet, palletDto);

        // Preparation hours:
        newPallet.setPreparationTime(palletDto.getPreparationTime());
        newPallet.setPackaging(palletDto.getPackaging());
        newPallet.setTag(palletDto.getTag());
        //newPallet.setTotalNet(palletDto.getTotalNet());

        return palletRepository.save(newPallet);
    }
}
