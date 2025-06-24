package com.example.medjool.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** * Represents an order in the system.
 * An order is associated with a client and contains multiple order items.
 * It includes details such as total price, currency, total weight, production date,
 * delivery date, shipping address, and status.
 */


@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private MixedOrderItem mixedOrderItem;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private OrderCurrency currency;

    @Column(name = "total_weight", nullable = false)
    private double totalWeight;


    @Column(name = "production_date")
    private LocalDateTime productionDate;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(length = 30) // optional, to avoid truncation for long enum names
    private OrderStatus status;

    @Column(name = "working_hours",nullable = true)
    private double workingHours;

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}