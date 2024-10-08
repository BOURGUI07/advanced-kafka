package com.example.order.application.service;

import com.example.common.events.order.OrderStatus;
import com.example.order.application.repo.PurchaseOrderRepo;
import com.example.order.common.dto.OrderShipmentSchedule;
import com.example.order.common.service.shipping.ShippingComponentStatusListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ShippingComponentService implements ShippingComponentStatusListener {
    private final PurchaseOrderRepo repo;
    @Override
    public Mono<Void> onSuccess(OrderShipmentSchedule message) {
        return repo.findByOrderIdAndStatus(message.orderId(), OrderStatus.COMPLETED)
                .doOnNext(o->o.setDeliveryDate(message.deliveryDate()))
                .then();
    }

    @Override
    public Mono<Void> onFailure(OrderShipmentSchedule message) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> onRollBack(OrderShipmentSchedule message) {
        return Mono.empty();
    }
}
