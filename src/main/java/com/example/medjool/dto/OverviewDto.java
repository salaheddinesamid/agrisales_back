package com.example.medjool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverviewDto
{

    Double totalStock;
    Long totalOrders;


    double totalOrdersPreProduction;
    double totalOrdersPostProduction;
    Long totalShippedOrders;

    double totalPreProductionRevenue;
    double totalPostProductionRevenue;
    double totalShippedRevenue;
    double totalRevenue;
}
