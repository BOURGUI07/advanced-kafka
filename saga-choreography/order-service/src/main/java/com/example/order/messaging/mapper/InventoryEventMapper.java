package com.example.order.messaging.mapper;

import com.example.common.events.inventory.InventoryEvent;
import com.example.common.events.inventory.InventoryStatus;
import com.example.common.events.payment.PaymentEvent;
import com.example.common.events.payment.PaymentStatus;
import com.example.order.common.dto.OrderInventoryDTO;
import com.example.order.common.dto.OrderPaymentDTO;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventMapper {
    public OrderInventoryDTO toRestoredDTO(InventoryEvent.Restored event) {
        return OrderInventoryDTO.builder()
                .orderId(event.orderId())
                .status(InventoryStatus.RESTORED)
                .inventoryId(event.inventoryId())
                .build();
    }

    public OrderInventoryDTO toDeclinedDTO(InventoryEvent.Declined event) {
        return OrderInventoryDTO.builder()
                .orderId(event.orderId())
                .status(InventoryStatus.DECLINED)
                .build();
    }

    public OrderInventoryDTO toDeductedDTO(InventoryEvent.Deducted event) {
        return OrderInventoryDTO.builder()
                .orderId(event.orderId())
                .inventoryId(event.inventoryId())
                .status(InventoryStatus.DEDUCTED)
                .build();
    }
}
