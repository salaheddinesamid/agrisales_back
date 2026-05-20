package com.example.medjool.modules.order.repository;

import com.example.medjool.modules.order.model.MixedOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MixedOrderItemRepo extends JpaRepository<MixedOrderItem, Long> {
}
