package com.example.order.common.service;

import com.example.order.common.dto.PurchaseOrderDTO;

public interface OrderEventListener {
    void emitCreatedOrder(PurchaseOrderDTO purchaseOrderDTO);
}
