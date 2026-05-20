package com.example.medjool.modules.order.service.implementation;

import com.example.medjool.exception.OrderCannotBeCanceledException;
import com.example.medjool.modules.order.model.Order;
import com.example.medjool.modules.order.model.OrderHistory;
import com.example.medjool.modules.order.model.OrderItem;
import com.example.medjool.modules.order.model.OrderStatus;
import com.example.medjool.modules.order.repository.OrderHistoryRepository;
import com.example.medjool.modules.order.repository.OrderRepository;
import com.example.medjool.modules.order.service.OrderCancellerService;
import com.example.medjool.modules.production.dto.ProductionRequestDto;
import com.example.medjool.modules.production.dto.ProductionResponseDto;
import com.example.medjool.modules.stock.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class OrderCancellerServiceImpl implements OrderCancellerService {

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    @Autowired
    public OrderCancellerServiceImpl(OrderRepository orderRepository, OrderHistoryRepository orderHistoryRepository) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @Override
    public void cancelOrder(Long id) {

    }
}
