package com.example.order.application.entity;


import com.example.common.events.inventory.InventoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Table(name="order_inventory")
public class OrderInventory {
    @Id
    private Integer id;
    private UUID orderId;
    private UUID inventoryId;
    private Boolean success;
    private InventoryStatus status;
    private String message;
}
