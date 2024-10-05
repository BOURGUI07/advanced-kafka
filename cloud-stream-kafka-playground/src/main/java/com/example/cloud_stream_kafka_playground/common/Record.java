package com.example.cloud_stream_kafka_playground.common;

import reactor.kafka.receiver.ReceiverOffset;

public record Record<T>(
        String key,
        T message,
        ReceiverOffset acknowledgement
) {
}
