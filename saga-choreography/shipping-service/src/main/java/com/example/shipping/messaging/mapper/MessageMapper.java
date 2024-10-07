package com.example.shipping.messaging.mapper;

import com.example.common.events.order.OrderEvent;
import com.example.common.events.shipping.ShippingEvent;
import com.example.shipping.common.dto.ScheduleRequest;
import com.example.shipping.common.dto.ShipmentDTO;

import java.time.Instant;

public class MessageMapper {
    public static ScheduleRequest toScheduleRequest(OrderEvent.Created event) {
        return ScheduleRequest.builder()
                .customerId(event.customerId())
                .productId(event.productId())
                .quantity(event.quantity())
                .orderId(event.orderId())
                .build();
    }

    public static ShippingEvent toShippingScheduledEvent(ShipmentDTO dto) {
        return ShippingEvent.Scheduled.builder()
                .shippingId(dto.shipmentId())
                .orderId(dto.orderId())
                .createdAt(Instant.now())
                .expectedDelivery(dto.deliveryDate())
                .build();
    }
}
