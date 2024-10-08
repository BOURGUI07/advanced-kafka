package com.example.order.application.entity;

import com.example.common.events.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Table(name="purchase_order")
public class PurchaseOrder {
    @Id
    private UUID orderId;
    private Integer customerId;
    private Integer productId;
    private Integer quantity;
    private Integer unitPrice;
    private Integer amount;
    private OrderStatus status;
    private Instant deliveryDate;
    private Integer version;
}
