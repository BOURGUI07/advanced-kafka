package com.example.payment;

import com.example.common.events.order.OrderEvent;
import com.example.common.events.payment.PaymentEvent;
import com.example.payment.application.repo.CustomerRepo;
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
        "spring.cloud.function.definition=processor;orderEventProducer;paymentEventConsumer",
        "spring.cloud.stream.bindings.orderEventProducer-out-0.destination=order-events",
        "spring.cloud.stream.bindings.paymentEventConsumer-in-0.destination=payment-events"
})
public class PaymentServiceTest extends AbstractIntegrationTests{
    private static final Sinks.Many<OrderEvent> reqSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Sinks.Many<PaymentEvent> resSink = Sinks.many().unicast().onBackpressureBuffer();
    private static final Flux<PaymentEvent> resFlux = resSink.asFlux().cache(0);
    @Autowired
    private CustomerRepo repo;


    @TestConfiguration
    static class TestConfig {

        @Bean
        public Supplier<Flux<OrderEvent>> orderEventProducer(){
            return reqSink::asFlux;
        }
        @Bean
        public Consumer<Flux<PaymentEvent>> paymentEventConsumer(){
            return f->f.doOnNext(resSink::tryEmitNext).subscribe();
        }
    }


    @Test
    void deductAndRefundTest(){
        //deduct payment
        var orderCreatedEvent = TestDataUtil.createOrderCreatedEvent(1,1,2,3);
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(orderCreatedEvent))
                .next()
                .timeout(Duration.ofSeconds(1))
                .cast(PaymentEvent.Deducted.class)
                .as(StepVerifier::create)
                .consumeNextWith(e ->{
                    Assertions.assertNotNull(e.paymentId());
                    Assertions.assertEquals(1,e.customerId());
                    Assertions.assertEquals(6,e.amount());
                    Assertions.assertEquals(orderCreatedEvent.orderId(),e.orderId());
                }).verifyComplete();

        //check Balance
        repo.findById(1)
                .as(StepVerifier::create)
                .consumeNextWith(c->Assertions.assertEquals(94,c.getBalance()))
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
                .cast(PaymentEvent.Refunded.class)
                .as(StepVerifier::create)
                .consumeNextWith(e ->{
                    Assertions.assertNotNull(e.paymentId());
                    Assertions.assertEquals(1,e.customerId());
                    Assertions.assertEquals(6,e.amount());
                    Assertions.assertEquals(orderCreatedEvent.orderId(),e.orderId());
                }).verifyComplete();

        //check balance
        repo.findById(1)
                .as(StepVerifier::create)
                .consumeNextWith(c->Assertions.assertEquals(100,c.getBalance()))
                .verifyComplete();
    }

    @Test
    void refundWithoutDeductTest(){
        var cancelledEvent = TestDataUtil.createOrderCancelledEvent(UUID.randomUUID());
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(cancelledEvent))
                .next()
                .timeout(Duration.ofSeconds(1),Mono.empty())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void customerNotFoundTest(){
        var orderCreatedEvent = TestDataUtil.createOrderCreatedEvent(10,1,2,3);
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(orderCreatedEvent))
                .next()
                .timeout(Duration.ofSeconds(1))
                .cast(PaymentEvent.Declined.class)
                .as(StepVerifier::create)
                .consumeNextWith(e ->{
                    Assertions.assertEquals(10,e.customerId());
                    Assertions.assertEquals(6,e.amount());
                    Assertions.assertEquals(orderCreatedEvent.orderId(),e.orderId());
                    Assertions.assertEquals("Customer with id 10 not found",e.message());
                }).verifyComplete();
    }

    @Test
    void insufficientBalanceTest(){
        var orderCreatedEvent = TestDataUtil.createOrderCreatedEvent(1,1,140,3);
        resFlux
                .doFirst(() -> reqSink.tryEmitNext(orderCreatedEvent))
                .next()
                .timeout(Duration.ofSeconds(1))
                .cast(PaymentEvent.Declined.class)
                .as(StepVerifier::create)
                .consumeNextWith(e ->{
                    Assertions.assertEquals(1,e.customerId());
                    Assertions.assertEquals(420,e.amount());
                    Assertions.assertEquals(orderCreatedEvent.orderId(),e.orderId());
                    Assertions.assertEquals("Insufficient balance for customer with id 1",e.message());
                }).verifyComplete();
    }
}
