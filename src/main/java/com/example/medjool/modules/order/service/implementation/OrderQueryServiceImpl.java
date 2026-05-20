package com.example.medjool.modules.order.service.implementation;

import com.example.medjool.modules.order.dto.OrderHistoryResponseDto;
import com.example.medjool.modules.order.dto.OrderResponseDto;
import com.example.medjool.modules.order.repository.OrderHistoryRepository;
import com.example.medjool.modules.order.repository.OrderRepository;
import com.example.medjool.modules.order.service.OrderQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderQueryServiceImpl(OrderHistoryRepository orderHistoryRepository, OrderRepository orderRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponseDto::new)
                .orElse(null);
    }

    @Override
    public List<OrderHistoryResponseDto> getAllOrderHistory() {
        return orderHistoryRepository.findAll()
                .stream().filter(orderHistory -> orderHistory.getReceivedAt() == null)
                .map(OrderHistoryResponseDto::new)
                .toList();
    }
}
