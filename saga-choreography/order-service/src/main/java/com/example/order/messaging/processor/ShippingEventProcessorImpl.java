package com.example.order.messaging.processor;

import com.example.common.events.DomainEvent;
import com.example.common.events.order.OrderEvent;
import com.example.common.events.shipping.ShippingEvent;
import com.example.common.processor.ShippingProcessor;
import com.example.order.common.service.shipping.ShippingComponentStatusListener;
import com.example.order.messaging.mapper.ShippingEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ShippingEventProcessorImpl implements ShippingProcessor<OrderEvent> {
    private final ShippingEventMapper mapper;
    private final ShippingComponentStatusListener listener;


    @Override
    public Mono<OrderEvent> handle(ShippingEvent.Scheduled e) {
        var dto = mapper.toDto(e);
        return listener.onSuccess(dto)
                .then(Mono.empty());
    }
}
