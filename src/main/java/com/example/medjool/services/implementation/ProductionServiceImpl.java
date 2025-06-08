package com.example.medjool.services.implementation;

import com.example.medjool.dto.FactoryScheduleResponseDto;
import com.example.medjool.dto.ProductionDetailsResponseDto;
import com.example.medjool.model.FactorySchedule;
import com.example.medjool.model.Order;
import com.example.medjool.model.ProductionOrder;
import com.example.medjool.model.ProductionStatus;
import com.example.medjool.repository.FactoryScheduleRepository;
import com.example.medjool.repository.OrderRepository;
import com.example.medjool.repository.ProductionOrderRepository;
import com.example.medjool.services.ProductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductionServiceImpl implements ProductionService {

    private final ProductionOrderRepository productionOrderRepository;
    private final OrderRepository orderRepository;
    private final FactoryScheduleRepository factoryScheduleRepository;

    @Autowired
    public ProductionServiceImpl(ProductionOrderRepository productionOrderRepository, OrderRepository orderRepository, FactoryScheduleRepository factoryScheduleRepository) {
        this.productionOrderRepository = productionOrderRepository;
        this.orderRepository = orderRepository;
        this.factoryScheduleRepository = factoryScheduleRepository;
    }

    @Override
    public void pushIntoProduction(Long orderId, LocalDateTime startDate) throws Exception {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));
        if (productionOrderRepository.existsById(orderId)) {
            throw new Exception("Order already in production");
        }

        ProductionOrder productionOrder = new ProductionOrder();
        productionOrder.setStartDate(startDate);
        productionOrder.setOrder(order);
        productionOrder.setWorkingHours(order.getWorkingHours());
        productionOrder.setRemainingHours(order.getWorkingHours());
        productionOrder.setProductionStatus(ProductionStatus.NOT_STARTED);
        System.out.println("Pushing order " + orderId + " into production starting from " + startDate);

        productionOrderRepository.save(productionOrder);
    }

    @Override
    public ResponseEntity<List<ProductionDetailsResponseDto>> getAllProductionDetails() throws Exception {
        List<ProductionOrder> productionOrders = productionOrderRepository.findAll();
        return ResponseEntity.ok(productionOrders.stream()
                .map(ProductionDetailsResponseDto::new).toList());
    }

    @Override
    public ResponseEntity<List<FactoryScheduleResponseDto>> getFactorySchedule() {
        List<FactorySchedule> factorySchedules = factoryScheduleRepository.findAll();
        List<FactoryScheduleResponseDto> response = factorySchedules.stream()
                .map(factorySchedule-> {
                    FactoryScheduleResponseDto dto = new FactoryScheduleResponseDto();
                    dto.setDate(factorySchedule.getDate());
                    dto.setRemainingHours(factorySchedule.getRemainingHours());

                    dto.setWorkingHours(factorySchedule.getWorkingHours());
                    dto.setScheduledOrders(factorySchedule.getOrders().stream().map(Order::getId).toList());
                    dto.setAvailable(factorySchedule.getIsAvailable());
                    return dto;
                }).toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
