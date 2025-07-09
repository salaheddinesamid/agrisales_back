package com.example.medjool.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "mixed_order_items")
public class MixedOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

     @JoinColumn(name = "pallet_id")
     @ManyToOne(fetch = FetchType.LAZY)
     private Pallet pallet;

    @OneToMany(mappedBy = "mixedOrderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MixedOrderItemDetails> itemDetails;
}
