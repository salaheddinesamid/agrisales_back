package com.example.medjool.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/** * Represents a pallet in the production system.
 * Contains information about packaging, costs, dimensions, and other attributes.
 */
@Entity
@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
public class Pallet {

    // Basic information:
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer palletId;

    @Column(name = "packaging")
    private float packaging;

    @Column(name = "number_of_boxes_in_carton")
    private Integer numberOfBoxesInCarton;

    @Column(name = "number_of_cartons_in_story")
    private Integer numberOfCartonsInStory;

    @Column(name = "number_of_stories_in_pallet")
    private Integer numberOfStoriesInPallet;

    @Column(name = "number_of_boxes_in_story")
    private Integer numberOfBoxesInStory;

    @Column(name = "number_of_boxes_in_pallet")
    private Integer numberOfBoxesInPallet;

    // Costs:
    @Column(name = "production_cost")
    private Float productionCost;
    @Column(name = "date_purchase")
    private Float datePurchase;
    @Column(name = "labor_cost")
    private Float laborCost;
    @Column(name = "packaging_cost")
    private Float packagingCost;
    @Column(name = "fuel_cost")
    private Float fuelCost;
    @Column(name = "transportation_cost")
    private Float transportationCost;
    @Column(name = "packaging_at")
    private Float packagingAT;
    @Column(name = "labor_transport_cost")
    private Float laborTransportCost;
    @Column(name = "markUpCost")
    private Float markUpCost;
    @Column(name = "vat")
    private Float vat;
    @Column(name = "preliminary_logistics_cost")
    private Float preliminaryLogisticsCost;
    @Column(name = "insurance_cost")
    private Float insuranceCost;


    // Dimensions:
    @Column(name = "height")
    private float height;

    @Column(name = "width")
    private float width;

    @Column(name = "length")
    private float length;

    @Column(name = "total_net")
    private Float totalNet;


    @Column(name = "tag")
    private String tag;

    @Column(name = "notes")
    private String notes;

    @Column(name = "preparation_time_in_hours")
    private double preparationTime;

    public float getTotalPalletCost(){
        return productionCost +
                datePurchase + laborCost +
                packagingCost + fuelCost + transportationCost +
                packagingAT + laborTransportCost + markUpCost + vat +
                preliminaryLogisticsCost + insuranceCost;
    }


}
