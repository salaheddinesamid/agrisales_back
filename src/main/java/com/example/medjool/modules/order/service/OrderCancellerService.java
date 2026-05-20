package com.example.medjool.modules.order.service;

public interface OrderCancellerService {
    /**
     * * Cancels an order by its ID.
     * @param id the ID of the order to cancel
     */
    void cancelOrder(Long id);
}
