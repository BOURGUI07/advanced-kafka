package com.example.shipping;

import com.example.common.events.inventory.InventoryEvent;
import com.example.common.events.order.OrderEvent;
import com.example.common.events.shipping.ShippingEvent;
import com.example.common.events.shipping.ShippingStatus;
import com.example.shipping.application.repo.ShipmentRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:paymentdb",
        "spring.cloud.function.definition=processor;orderEventProducer;shippingEventConsumer",
        "spring.cloud.stream.bindings.orderEventProducer-out-0.destination=order-events",
        "spring.cloud.stream.bindings.shippingEventConsumer-in-0.destination=shipping-events"
})
public class ShippingServiceTest extends AbstractIntegrationTests {
    private static final Sinks.Many<OrderEvent> reqSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<ShippingEvent> resSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Flux<ShippingEvent> resFlux = resSink.asFlux().cache(0);
    @Autowired
    private ShipmentRepo repo;


    @TestConfiguration
    static class TestConfig {

        @Bean
        public Supplier<Flux<OrderEvent>> orderEventProducer(){
            return reqSink::asFlux;
        }
        @Bean
        public Consumer<Flux<ShippingEvent>> shippingEventConsumer(){
            return f->f.doOnNext(resSink::tryEmitNext).subscribe();
        }
    }


    @Test
    public void planAndCancelTest(){

        // emit created event, expect no event
        var createdEvent = TestDataUtil.createOrderCreatedEvent(1, 1, 2, 3);
        expectNoEvent(createdEvent);

        // duplicate event, expect no event
        expectNoEvent(createdEvent);

        // check table for just 1 record
        this.repo.findByOrderIdAndStatus(createdEvent.orderId(), ShippingStatus.PENDING)
                .as(StepVerifier::create)
                .consumeNextWith(s -> {
                    Assertions.assertEquals(createdEvent.orderId(), s.getOrderId());
                    Assertions.assertEquals(createdEvent.quantity(), s.getQuantity());
                    Assertions.assertNull(s.getDeliveryDate());
                })
                .verifyComplete();

        // emit order cancelled event
        var cancelledEvent = TestDataUtil.createOrderCancelledEvent(createdEvent.orderId());
        expectNoEvent(cancelledEvent);

        // check table for 0 record
        this.repo.findByOrderIdAndStatus(createdEvent.orderId(), ShippingStatus.PENDING)
                .as(StepVerifier::create)
                .verifyComplete();

    }

    @Test
    public void planAndScheduleTest(){

        // emit created event, expect no event
        var createdEvent = TestDataUtil.createOrderCreatedEvent(1, 1, 2, 3);
        expectNoEvent(createdEvent);

        // emit order cancelled event
        var completedEvent = TestDataUtil.createOrderCompletedEvent(createdEvent.orderId());
        expectEvent(completedEvent, ShippingEvent.Scheduled.class, e -> {
            Assertions.assertEquals(createdEvent.orderId(), e.orderId());
            Assertions.assertNotNull(e.shippingId());
            Assertions.assertNotNull(e.expectedDelivery());
        });

        // duplicate completed event
        expectNoEvent(completedEvent);
    }

    private <T> void expectEvent(OrderEvent event, Class<T> type, Consumer<T> assertion){
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(event))
                .next()
                .timeout(Duration.ofSeconds(1), Mono.empty())
                .cast(type)
                .as(StepVerifier::create)
                .consumeNextWith(assertion)
                .verifyComplete();
    }

    private void expectNoEvent(OrderEvent event){
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(event))
                .next()
                .timeout(Duration.ofSeconds(1), Mono.empty())
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
