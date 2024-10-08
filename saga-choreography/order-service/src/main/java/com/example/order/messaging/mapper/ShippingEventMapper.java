package com.example.order.messaging.mapper;

import com.example.common.events.shipping.ShippingEvent;
import com.example.order.common.dto.OrderShipmentSchedule;
import org.springframework.stereotype.Component;

@Component
public class ShippingEventMapper {
    public  OrderShipmentSchedule toDto(ShippingEvent.Scheduled event) {
        return OrderShipmentSchedule.builder()
                .orderId(event.orderId())
                .deliveryDate(event.expectedDelivery())
                .build();
    }
}
