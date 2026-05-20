package com.example.medjool.modules.analytics.service.implementation;

import com.example.medjool.modules.analytics.helpers.ClientMarginHelper;
import com.example.medjool.modules.client.repository.ClientRepository;
import com.example.medjool.modules.notification.service.implementation.AlertServiceImpl;
import com.example.medjool.modules.order.dto.MarginClientResponseDto;
import com.example.medjool.modules.order.repository.OrderRepository;
import com.example.medjool.modules.settings.model.SystemSetting;
import com.example.medjool.modules.analytics.dto.OverviewDto;
import com.example.medjool.modules.analytics.service.AnalyticsQueryService;
import com.example.medjool.modules.order.model.Order;
import com.example.medjool.modules.order.model.OrderStatus;
import com.example.medjool.modules.stock.model.Product;
import com.example.medjool.modules.stock.repository.ProductRepository;
import com.example.medjool.modules.stock.service.implementation.StockQueryServiceImpl;
import com.example.medjool.modules.user_management.service.implementation.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class AnalyticsQueryServiceImpl implements AnalyticsQueryService {

    private final OrderRepository orderRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final ProductRepository productRepository;
    private final AlertServiceImpl alertService;
    private final ClientMarginHelper clientMarginHelper;
    private final ClientRepository clientRepository;

    @Autowired
    public AnalyticsQueryServiceImpl(OrderRepository orderRepository, SystemSettingRepository systemSettingRepository, ProductRepository productRepository, AlertServiceImpl alertService, ClientMarginHelper clientMarginHelper, ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.productRepository = productRepository;
        this.alertService = alertService;
        this.clientMarginHelper = clientMarginHelper;
        this.clientRepository = clientRepository;
    }

    @Override
    public OverviewDto getOverview() {
        final String STOCK_KEY = "min_stock_level";

        // Defensive check for system setting
        double minimumStockValue = systemSettingRepository.findByKey(STOCK_KEY)
                .map(SystemSetting::getValue)
                .orElseThrow(() -> new IllegalStateException("System setting 'min_stock_level' not found"));

        List<Product> products = productRepository.findAll(); // Fetch all products

        // Calculate total stock weight and check for low stock
        double totalStockWeight = products.stream()
                .mapToDouble(product -> {
                    double weight = product.getTotalWeight();
                    if (weight <= minimumStockValue) {
                        String alert = String.format("The product: %s is below the minimum stock level of %.2f", product.getProductId(), minimumStockValue);
                        alertService.newAlert(alert);
                    }
                    return weight;
                }).sum();



        List<Order> orders = orderRepository.findAll(); // Fetch all orders
        long totalOrders = orders.size(); // Return the total number of orders

        // Calculate total pre-production orders
        double totalOrdersPreProduction = orders.stream().map(order -> {
            if(order.getStatus().equals(OrderStatus.PRELIMINARY) ||
                    order.getStatus().equals(OrderStatus.CONFIRMED) ||
                    order.getStatus().equals(OrderStatus.IN_PRODUCTION)) {
                return 1.0; // Count pre-production orders
            }
            return 0.0;
        }).reduce(0.0, Double::sum);

        // Calculate total post-production orders
        double totalOrdersPostProduction = orders.stream().map(order -> {
            if(order.getStatus().equals(OrderStatus.READY_TO_SHIPPED)) {
                return 1.0; // Count pre-production orders
            }
            return 0.0;
        }).reduce(0.0, Double::sum);;

        // Calculate total shipped orders
        long totalShippedOrders = orders.stream().map(order -> {
            if (order.getStatus().equals(OrderStatus.SHIPPED)) {
                return 1L; // Count shipped orders
            }
            return 0L; // Not a shipped order
        }).reduce(0L, Long::sum);

        // Calculate total shipped revenue
        double totalShippedRevenue = orders.stream().map(order -> {
            if (order.getStatus().equals(OrderStatus.SHIPPED)) {
                return order.getTotalPrice() * order.getForex().getBuyingRate();
            }
            return 0.0; // Not a shipped order
        }).reduce(0.0, Double::sum);

        // Calculate total pre-production revenue
        double totalPreProductionRevenue = orders.stream().map(order -> {
            if(order.getStatus().equals(OrderStatus.PRELIMINARY) ||
                    order.getStatus().equals(OrderStatus.CONFIRMED) ||
                    order.getStatus().equals(OrderStatus.IN_PRODUCTION)) {
                return order.getTotalPrice() * order.getForex().getBuyingRate();
            }
            return 0.0;
        }).reduce(0.0, Double::sum);

        // Calculate total post-production revenue
        double totalPostProductionRevenue = orders.stream().map(order -> {
            if(order.getStatus().equals(OrderStatus.READY_TO_SHIPPED)) {
                return order.getTotalPrice() * order.getForex().getBuyingRate();
            }
            return 0.0;
        }).reduce(0.0, Double::sum);

        // Calculate total revenue across all orders
        double totalRevenue = orders.stream().map(order -> order.getTotalPrice() * order.getForex().getBuyingRate()).reduce(0.0, Double::sum);;

        return new OverviewDto(
                totalStockWeight,
                totalOrders,
                totalOrdersPreProduction,
                totalOrdersPostProduction,
                totalShippedOrders,
                totalPreProductionRevenue,
                totalPostProductionRevenue,
                totalShippedRevenue,
                totalRevenue
        );
    }

    @Override
    public List<MarginClientResponseDto> getAllMarginPerClient(String productCode) {
        return clientRepository.findAll()
                .stream()
                .map(client -> clientMarginHelper.clientMargin(client.getCompanyName(), productCode))
                .filter(Objects::nonNull)
                .toList();
    }
}
