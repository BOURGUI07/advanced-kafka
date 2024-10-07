package com.example.shipping.application.mapper;

import com.example.shipping.application.entity.Shipment;
import com.example.shipping.common.dto.ScheduleRequest;
import com.example.shipping.common.dto.ShipmentDTO;
import org.springframework.stereotype.Component;

@Component
public class ShipmentMapper {
    public Shipment toEntity(ScheduleRequest request){
        return Shipment.builder()
                .customerId(request.customerId())
                .productId(request.productId())
                .orderId(request.orderId())
                .quantity(request.quantity())
                .build();
    }

    public ShipmentDTO toDto(Shipment entity){
        return ShipmentDTO.builder()
                .customerId(entity.getCustomerId())
                .productId(entity.getProductId())
                .orderId(entity.getOrderId())
                .quantity(entity.getQuantity())
                .shipmentId(entity.getId())
                .status(entity.getStatus())
                .deliveryDate(entity.getDeliveryDate())
                .build();
    }
}
