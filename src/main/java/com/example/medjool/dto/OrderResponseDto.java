package com.example.medjool.dto;

import com.example.medjool.model.MixedOrderItem;
import com.example.medjool.model.MixedOrderItemDetails;
import com.example.medjool.model.Order;
import com.example.medjool.utils.DateFormatter;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponseDto {

    private DateFormatter dateFormatter = new DateFormatter();
    private Long id;
    private String clientName;
    private double totalPrice;
    private String currency;
    private double totalWeight;
    private String status;

    private List<OrderItemResponseDto> items;
    private MixedOrderResponseDto mixedOrder;
    private String productionDate;
    private LocalDateTime deliveryDate;
    private double workingHours;
    private String shippingAddress;

    public OrderResponseDto(Order order) {
        this.id = order.getId();
        this.clientName = order.getClient().getCompanyName();
        this.totalPrice = order.getTotalPrice();
        this.currency = order.getCurrency().toString();
        this.totalWeight = order.getTotalWeight();
        this.status = order.getStatus().toString();
        this.items = order.getOrderItems().stream()
                .map(OrderItemResponseDto::new)
                .toList();
        this.mixedOrder = order.getMixedOrderItem() != null ? new MixedOrderResponseDto(order.getMixedOrderItem()) : null;
        this.productionDate = dateFormatter.formatDate(order.getProductionDate());
        this.deliveryDate = order.getDeliveryDate();
        this.workingHours = order.getWorkingHours();
        this.shippingAddress  = order.getShippingAddress();
    }
}

@Data
class MixedOrderResponseDto{

    private List<MixedOrderItemDto> items;
    private double totalWeight;
    private double totalPrice;
    private int palletId;

    public MixedOrderResponseDto(MixedOrderItem mixedOrderItem){
        this.items = mixedOrderItem.getItemDetails().stream()
                .map(MixedOrderItemDto::new)
                .toList();
        this.palletId = mixedOrderItem.getPallet().getPalletId();
        this.totalWeight = mixedOrderItem.getTotalWeight();
        this.totalPrice = mixedOrderItem.getTotalPrice();
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class MixedOrderItemDto{
    private String productCode;
    private String brand;
    private double percentage;
    private double pricePerKg;
    private double weight;
    private double price;

    public MixedOrderItemDto(MixedOrderItemDetails mixedOrderItemDetails){
        this.productCode = mixedOrderItemDetails.getProduct().getProductCode();
        this.percentage = mixedOrderItemDetails.getPercentage();
        this.brand = mixedOrderItemDetails.getBrand();
        this.pricePerKg = mixedOrderItemDetails.getPricePerKg();
        this.weight = mixedOrderItemDetails.getWeight();
        this.price = mixedOrderItemDetails.getTotalPrice();
    }
}