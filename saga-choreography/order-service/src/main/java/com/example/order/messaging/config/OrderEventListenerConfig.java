package com.example.order.messaging.config;

import com.example.common.events.order.OrderEvent;
import com.example.order.common.service.OrderEventListener;
import com.example.order.messaging.publisher.OrderEventListenerEventImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;
@Configuration
public class OrderEventListenerConfig {

    @Bean
    public OrderEventListener orderEventListener() {
        var sink = Sinks.many().unicast().<OrderEvent>onBackpressureBuffer();
        var flux = sink.asFlux();
        return new OrderEventListenerEventImpl(flux,sink);
    }
}
