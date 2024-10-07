package com.example.inventory.application.mapper;

import com.example.inventory.application.entity.OrderInventory;
import com.example.inventory.common.dto.InventoryDTO;
import com.example.inventory.common.dto.InventoryProcessRequest;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {
    public OrderInventory toEntity(InventoryProcessRequest request) {
        return OrderInventory.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .orderId(request.orderId())
                .build();
    }

    public InventoryDTO toDto(OrderInventory entity) {
        return InventoryDTO.builder()
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .orderId(entity.getOrderId())
                .inventoryId(entity.getInventoryId())
                .orderStatus(entity.getStatus())
                .build();
    }
}
