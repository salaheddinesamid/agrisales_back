package com.example.medjool.modules.notification.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification") // Explicit table name
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    private String content;

    @Column(name = "is_read") // Changed from 'read' to avoid SQL keyword conflict
    private boolean read;
}