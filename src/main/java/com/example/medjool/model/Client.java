package com.example.medjool.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

/** * Represents a client in the system.
 * Contains information about the client's company, contacts, and addresses.
 */


@Entity
@Getter
@Setter
@AllArgsConstructor
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

    @Column(name = "SIRET")
    private String SIRET;

    @Column(name = "web_site")
    private String webSite;

    @Column(name = "preferred_product_quality")
    private String preferredProductQuality;

    @Column(name = "commission", nullable = true)
    private Float commission;

    @OneToMany
    List<Address> addresses;

    @OneToMany
    List<Contact> contacts;

    @Enumerated(EnumType.STRING)
    ClientStatus clientStatus;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Order> orders;

    public Client(){

    }


    public <E> Client(int id,String companyName,
                      String generalManager, String companyActivity,
                      String SIRET, String webSite, String preferredProductQuality,
                      Float commission, List<Address> address, List<Contact> contact, ClientStatus clientStatus) {

    }
}
