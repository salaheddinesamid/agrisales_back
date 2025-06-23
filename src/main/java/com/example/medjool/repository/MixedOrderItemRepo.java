package com.example.medjool.repository;

import com.example.medjool.model.MixedOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MixedOrderItemRepo extends JpaRepository<MixedOrderItem, Long> {
}
