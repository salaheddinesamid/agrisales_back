package com.example.medjool.component;

import com.example.medjool.model.*;
import com.example.medjool.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ProductionSchedulerService {

    private final ProductionOrderRepository productionOrderRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final FactoryScheduleRepository factoryScheduleRepository;

    private final Logger logger = LoggerFactory.getLogger(ProductionSchedulerService.class);

    @Autowired
    public ProductionSchedulerService(ProductionOrderRepository productionOrderRepository,
                                      SystemSettingRepository systemSettingRepository,
                                      FactoryScheduleRepository factoryScheduleRepository) {
        this.productionOrderRepository = productionOrderRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.factoryScheduleRepository = factoryScheduleRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // Run every 24 hours
    public void scheduleProductionOrders() {
        try {
            Optional<SystemSetting> factoryHoursSetting = systemSettingRepository.findByKey("factory_working_hours");
            if (!factoryHoursSetting.isPresent()) {
                logger.error("Factory working hours setting not found");
                return;
            }

            double factoryWorkingHours = factoryHoursSetting.get().getValue();
            if (factoryWorkingHours <= 0) {
                logger.error("Invalid factory working hours value: {}", factoryWorkingHours);
                return;
            }

            FactorySchedule factorySchedule = factoryScheduleRepository.findByDate(LocalDate.now());
            if (factorySchedule == null) {
                factorySchedule = createNewFactorySchedule(factoryWorkingHours);
            }

            List<ProductionOrder> productionOrderInProgress = productionOrderRepository.findAllByProductionStatus(ProductionStatus.IN_PROGRESS);
            List<ProductionOrder> productionOrderNotStarted = productionOrderRepository.findAllByProductionStatus(ProductionStatus.NOT_STARTED);

            scheduleInProgressOrders(factorySchedule, productionOrderInProgress, factoryWorkingHours);

            if (factorySchedule.getRemainingHours() > 0 && !productionOrderNotStarted.isEmpty()) {
                scheduleNotStartedOrders(factorySchedule, productionOrderNotStarted);
            }

            factoryScheduleRepository.save(factorySchedule);
        } catch (Exception e) {
            logger.error("Error in production scheduling: {}", e.getMessage(), e);
        }
    }

    private FactorySchedule createNewFactorySchedule(double factoryWorkingHours) {
        // No need to delete all records - just create a new one for today
        FactorySchedule factorySchedule = new FactorySchedule();
        factorySchedule.setDate(LocalDate.now());
        factorySchedule.setWorkingHours(factoryWorkingHours);
        factorySchedule.setRemainingHours(factoryWorkingHours);
        factorySchedule.setIsAvailable(true);
        return factoryScheduleRepository.save(factorySchedule);
    }

    @Transactional
    public void checkCompletedOrders() {
        List<ProductionOrder> productionOrders = productionOrderRepository.findAllByProductionStatus(ProductionStatus.COMPLETED);
        // TODO: Implement actual completion logic
        logger.info("Found {} completed orders", productionOrders.size());
    }

    private void scheduleInProgressOrders(FactorySchedule schedule, List<ProductionOrder> orders, double factoryWorkingHours) {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        List<Order> scheduledOrders = new ArrayList<>();
        double workingHoursRate = factoryWorkingHours / orders.size();

        for (ProductionOrder productionOrder : orders) {
            Order order = productionOrder.getOrder();
            double newRemainingHours = Math.max(0, productionOrder.getRemainingHours() - workingHoursRate);

            productionOrder.setRemainingHours(newRemainingHours);
            if (newRemainingHours <= 0) {
                productionOrder.setProductionStatus(ProductionStatus.COMPLETED);
            }

            productionOrderRepository.save(productionOrder);
            scheduledOrders.add(order);
        }

        double newRemainingScheduleHours = Math.max(0, schedule.getRemainingHours() - factoryWorkingHours);
        schedule.setRemainingHours(newRemainingScheduleHours);

        if (newRemainingScheduleHours <= 0) {
            schedule.setRemainingHours(0);
            schedule.setIsAvailable(false);
        }

        schedule.getOrders().addAll(scheduledOrders);
    }

    private void scheduleNotStartedOrders(FactorySchedule schedule, List<ProductionOrder> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        List<Order> scheduledOrders = new ArrayList<>();
        double remainingHours = schedule.getRemainingHours();
        double workingHoursRate = remainingHours / orders.size();

        for (ProductionOrder productionOrder : orders) {
            if (remainingHours <= 0) break;

            Order order = productionOrder.getOrder();
            productionOrder.setProductionStatus(ProductionStatus.IN_PROGRESS);

            double orderRemainingHours = productionOrder.getRemainingHours();
            if (orderRemainingHours <= 0) {
                orderRemainingHours = productionOrder.getWorkingHours();
            }

            double hoursToDeduct = Math.min(workingHoursRate, orderRemainingHours);
            productionOrder.setRemainingHours(orderRemainingHours - hoursToDeduct);

            if (productionOrder.getRemainingHours() <= 0) {
                productionOrder.setProductionStatus(ProductionStatus.COMPLETED);
            }

            productionOrderRepository.save(productionOrder);
            scheduledOrders.add(order);
            remainingHours -= hoursToDeduct;
        }

        schedule.setRemainingHours(remainingHours);
        if (remainingHours <= 0) {
            schedule.setIsAvailable(false);
        }

        schedule.getOrders().addAll(scheduledOrders);
    }
}