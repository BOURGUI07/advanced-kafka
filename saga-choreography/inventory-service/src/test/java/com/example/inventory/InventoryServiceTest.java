package com.example.inventory;

import com.example.common.events.inventory.InventoryEvent;
import com.example.common.events.order.OrderEvent;
import com.example.inventory.application.repo.ProductRepo;
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
        "spring.cloud.function.definition=processor;orderEventProducer;inventoryEventConsumer",
        "spring.cloud.stream.bindings.orderEventProducer-out-0.destination=order-events",
        "spring.cloud.stream.bindings.inventoryEventConsumer-in-0.destination=inventory-events"
})
public class InventoryServiceTest extends AbstractIntegrationTests{
    private static final Sinks.Many<OrderEvent> reqSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<InventoryEvent> resSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Flux<InventoryEvent> resFlux = resSink.asFlux().cache(0);
    @Autowired
    private ProductRepo repo;


    @TestConfiguration
    static class TestConfig {

        @Bean
        public Supplier<Flux<OrderEvent>> orderEventProducer(){
            return reqSink::asFlux;
        }
        @Bean
        public Consumer<Flux<InventoryEvent>> inventoryEventConsumer(){
            return f->f.doOnNext(resSink::tryEmitNext).subscribe();
        }
    }


    @Test
    void deductAndRestoreTest(){
        //deduct inventory
        var orderCreatedEvent = TestDataUtil.createOrderCreatedEvent(1,1,5,5);
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(orderCreatedEvent))
                .next()
                .timeout(Duration.ofSeconds(1))
                .cast(InventoryEvent.Deducted.class)
                .as(StepVerifier::create)
                .consumeNextWith(e ->{
                    Assertions.assertEquals(1,e.productId());
                    Assertions.assertEquals(5,e.quantity());
                    Assertions.assertEquals(orderCreatedEvent.orderId(),e.orderId());
                }).verifyComplete();

        //check Balance
        repo.findById(1)
                .as(StepVerifier::create)
                .consumeNextWith(c->Assertions.assertEquals(5,c.getAvailableQuantity()))
                .verifyComplete();

        //duplicate event
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(orderCreatedEvent))
                .next()
                .timeout(Duration.ofSeconds(1), Mono.empty())
                .as(StepVerifier::create)
                .verifyComplete();

        // cancelled event
        var cancelledEvent = TestDataUtil.createOrderCancelledEvent(orderCreatedEvent.orderId());
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(cancelledEvent))
                .next()
                .timeout(Duration.ofSeconds(1))
                .cast(InventoryEvent.Restored.class)
                .as(StepVerifier::create)
                .consumeNextWith(e ->{
                    Assertions.assertEquals(1,e.productId());
                    Assertions.assertEquals(5,e.quantity());
                    Assertions.assertEquals(orderCreatedEvent.orderId(),e.orderId());
                }).verifyComplete();

        //check balance
        repo.findById(1)
                .as(StepVerifier::create)
                .consumeNextWith(c->Assertions.assertEquals(10,c.getAvailableQuantity()))
                .verifyComplete();
    }

    @Test
    void restoreWithoutDeductTest(){
        var cancelledEvent = TestDataUtil.createOrderCancelledEvent(UUID.randomUUID());
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(cancelledEvent))
                .next()
                .timeout(Duration.ofSeconds(1),Mono.empty())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void productNotFoundTest(){
        var orderCreatedEvent = TestDataUtil.createOrderCreatedEvent(1,10,10,1);
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(orderCreatedEvent))
                .next()
                .timeout(Duration.ofSeconds(1))
                .cast(InventoryEvent.Declined.class)
                .as(StepVerifier::create)
                .consumeNextWith(e ->{
                    Assertions.assertEquals(10,e.productId());
                    Assertions.assertEquals(1,e.quantity());
                    Assertions.assertEquals(orderCreatedEvent.orderId(),e.orderId());
                    Assertions.assertEquals("Product with id 10 not found",e.message());
                }).verifyComplete();
    }

    @Test
    void outOfStockTest(){
        var orderCreatedEvent = TestDataUtil.createOrderCreatedEvent(1,1,1,14);
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(orderCreatedEvent))
                .next()
                .timeout(Duration.ofSeconds(1))
                .cast(InventoryEvent.Declined.class)
                .as(StepVerifier::create)
                .consumeNextWith(e ->{
                    Assertions.assertEquals(1,e.productId());
                    Assertions.assertEquals(14,e.quantity());
                    Assertions.assertEquals(orderCreatedEvent.orderId(),e.orderId());
                    Assertions.assertEquals("Product 1 is out of stock",e.message());
                }).verifyComplete();
    }
}
