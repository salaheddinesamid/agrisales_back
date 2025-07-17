package com.example.medjool.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @Column(name = "product_code")
    private String productCode;

    @Column(nullable = false)
    private String callibre;

    @Column(name = "total_weight", nullable = false)
    private Double totalWeight;

    @Column(nullable = false)
    private String farm;

    @Column(nullable = false)
    private String quality;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

}
