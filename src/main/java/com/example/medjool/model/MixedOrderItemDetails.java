package com.example.medjool.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

@Table(name = "mixed_order_item_details")
public class MixedOrderItemDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "mixed_order_id", nullable = false)
    private  MixedOrderItem mixedOrderItem;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "percentage",nullable = false)
    private double percentage;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(name = "price_per_kg",nullable = false)
    private double pricePerKg;
}
