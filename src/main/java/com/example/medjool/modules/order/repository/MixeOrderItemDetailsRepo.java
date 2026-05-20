package com.example.medjool.modules.order.repository;

import com.example.medjool.modules.order.model.MixedOrderItemDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MixeOrderItemDetailsRepo extends JpaRepository<MixedOrderItemDetails, Long> {
}
