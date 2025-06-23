package com.example.medjool.repository;

import com.example.medjool.model.MixedOrderItemDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MixeOrderItemDetailsRepo extends JpaRepository<MixedOrderItemDetails, Long> {
}
