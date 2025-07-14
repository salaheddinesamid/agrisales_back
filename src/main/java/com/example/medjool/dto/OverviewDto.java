package com.example.medjool.dto;

import lombok.Data;

@Data
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
