package com.example.medjool.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long shipmentId;

    private String trackingNumber;

    @Column(name = "tracking_url")
    private String trackingUrl;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Order order;

}
