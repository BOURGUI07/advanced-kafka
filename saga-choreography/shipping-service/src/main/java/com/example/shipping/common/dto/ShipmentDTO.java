package com.example.shipping.common.dto;

import com.example.common.events.shipping.ShippingStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ShipmentDTO(
        UUID shipmentId,
        UUID orderId,
        Integer productId,
        Integer quantity,
        Integer customerId,
        ShippingStatus status,
        Instant deliveryDate
) {
}
