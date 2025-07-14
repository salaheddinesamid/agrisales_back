package com.example.medjool.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "forex_table")
public class Forex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency",unique = true)
    @Enumerated(EnumType.STRING)
    private ForexCurrency currency;

    @Column(name = "buying_rate", nullable = false)
    private double buyingRate;
}
