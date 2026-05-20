package com.example.medjool.modules.settings.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "system_setting")
public class SystemSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "config_key") // renamed to avoid reserved keyword issue
    private String key;

    @Column(name = "value")
    private double value;

}
