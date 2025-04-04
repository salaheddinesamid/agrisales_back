package com.example.medjool.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "price_per_kg", nullable = false)
    private double pricePerKg;

    @Column(nullable = false)
    private double packaging;

    @Column(name = "number_of_pallets", nullable = false)
    private int numberOfPallets;

    @Column(name = "item_weight", nullable = false)
    private double itemWeight;
}