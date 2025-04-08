package com.example.medjool.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer clientId;

    @Column(name = "company_name")
    String companyName;

    @Column(name = "general_manager")
    String generalManager;

    @Column(name = "company_activity")
    String companyActivity;

    @OneToMany
    List<Address> addresses;

    @OneToMany
    List<Contact> contacts;

   @Enumerated(EnumType.STRING)
   ClientStatus clientStatus;
}
