package com.example.inventory.application.entity;

import com.example.common.events.inventory.InventoryStatus;
import com.example.common.events.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Accessors(chain = true)
@Table(name="order_inventory")
public class OrderInventory {
    @Id
    private UUID inventoryId;
    private UUID orderId;
    private Integer productId;
    private InventoryStatus status;
    private Integer quantity;
}
