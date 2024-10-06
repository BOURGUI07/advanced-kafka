package com.example.cloud_stream_kafka_playground.sec09.processor;

import com.example.cloud_stream_kafka_playground.common.MessageConverter;
import com.example.cloud_stream_kafka_playground.sec09.dto.DigitalDelivery;
import com.example.cloud_stream_kafka_playground.sec09.dto.OrderEvent;
import com.example.cloud_stream_kafka_playground.sec09.dto.OrderType;
import com.example.cloud_stream_kafka_playground.sec09.dto.PhysicalDelivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.function.Function;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class FanOutProcessor {
    private final Sinks.Many<OrderEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Bean
    public Function<Flux<Message<OrderEvent>>, Tuple2<Flux<DigitalDelivery>,Flux<PhysicalDelivery>>> processor(){
        return flux -> {
            flux
                    .map(MessageConverter::toRecord)
                    .doOnNext(r->this.sink.tryEmitNext(r.message()))
                    .doOnNext(r->r.acknowledgement().acknowledge())
                    .subscribe();
            return Tuples.of(
                    sink.asFlux().transform(toDigitalDelivery()),
                    sink.asFlux().filter(orderEvent-> OrderType.PHYSICAL.equals(orderEvent.type())).transform(toPhysicalDelivery())
            );
        };
    }


    private Function<Flux<OrderEvent>,Flux<DigitalDelivery>> toDigitalDelivery() {
        return flux -> flux.map(e->new DigitalDelivery(e.productId(), e.customerId()+"@gmail.com"));
    }

    private Function<Flux<OrderEvent>,Flux<PhysicalDelivery>>  toPhysicalDelivery() {
        return flux -> flux.map(e->{
            var customerId = e.customerId();
            return new PhysicalDelivery(e.productId(), customerId+"Street",customerId+"City",customerId+"Country");
        });
    }

}
