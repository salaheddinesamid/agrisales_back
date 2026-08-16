package com.example.medjool.modules.order.service.implementation;

import com.example.medjool.modules.order.dto.OrderRequestDto;
import com.example.medjool.modules.order.dto.OrderResponseDto;
import com.example.medjool.modules.order.service.OrderProcessorService;
import org.springframework.stereotype.Service;

@Service
public class MixedOrderProcessor implements OrderProcessorService {
    @Override
    public boolean supports(String orderType) {
        return false;
    }

    @Override
    public OrderResponseDto processOrder(OrderRequestDto orderRequestDto) {
        return null;
    }
}
