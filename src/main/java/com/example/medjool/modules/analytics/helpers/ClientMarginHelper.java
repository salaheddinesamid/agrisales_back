package com.example.medjool.modules.analytics.helpers;

import com.example.medjool.exception.ClientNotFoundException;
import com.example.medjool.modules.client.model.Client;
import com.example.medjool.modules.client.repository.ClientRepository;
import com.example.medjool.modules.order.dto.MarginClientResponseDto;
import com.example.medjool.modules.order.dto.OrderCostDto;
import com.example.medjool.modules.order.dto.OrderItemCostDto;
import com.example.medjool.modules.order.model.Order;
import com.example.medjool.modules.order.model.OrderCurrency;
import com.example.medjool.modules.order.model.OrderItem;
import com.example.medjool.modules.order.repository.OrderRepository;
import com.example.medjool.modules.stock.model.Pallet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientMarginHelper {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;

    public ClientMarginHelper(ClientRepository clientRepository, OrderRepository orderRepository) {
        this.clientRepository = clientRepository;
        this.orderRepository = orderRepository;
    }

    /** * Retrieves the margin for a specific client based on company name and product code.
     *
     * @param companyName the name of the company
     * @param productCode the product code to filter by, or "all" for all products
     * @return MarginClientResponseDto containing margin details for the client
     */
    public MarginClientResponseDto clientMargin(String companyName, String productCode){
        Client client = clientRepository.findByCompanyName(companyName);
        if(client == null) {
            throw new ClientNotFoundException();
        }
        List<Order> clientOrders = orderRepository.findAllByClient(client);
        List<Order> filteredOrders = clientOrders.stream().filter(order -> {
            if(!productCode.equals("all")) {
                return order.getOrderItems().stream().anyMatch(item -> item.getProduct().getProductCode().equals(productCode));
            }
            return true;
        }).toList();

        if(filteredOrders.isEmpty()) {
            return null;
        }

        List<OrderCostDto> ordersCost = filteredOrders
                .stream().map(order -> {
                    List<OrderItem> items = order.getOrderItems();
                    List<OrderItemCostDto> itemsCosts = items
                            .stream().map(item -> {
                                Pallet pallet = item.getPallet();
                                return new OrderItemCostDto(pallet, item.getNumberOfPallets());
                            }).toList();
                    double orderTotalCost = itemsCosts.stream()
                            .map(OrderItemCostDto::getTotal)
                            .reduce(0.0, Double::sum);

                    return new OrderCostDto(order.getId(),itemsCosts,orderTotalCost);
                }).toList();

        double totalWeight = filteredOrders.stream()
                .map(Order::getTotalWeight)
                .reduce(0.0, Double::sum);
        double totalRevenue = filteredOrders.stream()
                .map(clientOrder->{
                    if(clientOrder.getCurrency().equals(OrderCurrency.USD)) {
                        return clientOrder.getTotalPrice() * 10.5;
                    } else if(clientOrder.getCurrency().equals(OrderCurrency.EUR)) {
                        return clientOrder.getTotalPrice() * 11;
                    } else if(clientOrder.getCurrency().equals(OrderCurrency.MAD)) {
                        return clientOrder.getTotalPrice();
                    }
                    return 0.0;
                })
                .reduce(0.0, Double::sum);

        double totalOrdersCost = ordersCost.stream()
                .map(OrderCostDto::getTotalCost)
                .reduce(0.0, Double::sum);

        double marginOnVariableCost = totalRevenue - totalOrdersCost;
        double margin =  marginOnVariableCost / totalWeight;

        return new MarginClientResponseDto(
                companyName,
                totalWeight,
                totalRevenue,
                totalOrdersCost,
                marginOnVariableCost,
                margin
        );
    }
}
