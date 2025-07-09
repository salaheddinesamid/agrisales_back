package com.example.medjool.dto;

import com.example.medjool.model.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private String clientName;
    private List<OrderItemRequestDto> items;

    private MixedOrderDto mixedOrderDto;
    private String currency;
    private LocalDate productionDate;
    private  String shippingAddress;

    @Override
    public String toString(){
        return "OrderRequestDto{" +
                "clientName='" + clientName + '\'' +
                ", items=" + items +
                ", currency='" + currency + '\'' +
                ", productionDate=" + productionDate +
                ", shippingAddress='" + shippingAddress + '\'' +
                '}';
    }
}


