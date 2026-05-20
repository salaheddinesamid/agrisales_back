package com.example.medjool.modules.production.model;

import com.example.medjool.modules.order.model.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class FactorySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "working_hours")
    private double workingHours;

    @Column(name = "remaining_hours")
    private double remainingHours;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @OneToMany
    List<Order> orders;
}
