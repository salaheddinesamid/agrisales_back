package com.example.medjool.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private OrderCurrency orderCurrency;

    @Column(name = "price_per_kg", nullable = false)
    private double pricePerKg;


    @Column(nullable = false)
    private double packaging;

    @Column(name = "number_of_pallets", nullable = false)
    private int numberOfPallets;

    @Column(name = "item_weight", nullable = false)
    private double itemWeight;

    @Column(name = "brand")
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pallet_id", nullable = false)
    Pallet pallet;

    public OrderItem(
            Product product,
            double itemWeight,
            double pricePerKg,
            double packaging,
            int numberOfPallets,
            OrderCurrency orderCurrency,
            String brand,
            Pallet pallet,
            Order order
    ){
        this.product = product;
        this.itemWeight = itemWeight;
        this.pricePerKg = pricePerKg;
        this.packaging = packaging;
        this.numberOfPallets = numberOfPallets;
        this.orderCurrency = orderCurrency;
        this.brand = brand;
        this.pallet = pallet;
        this.order = order;
    }

    public OrderItem() {

    }
}