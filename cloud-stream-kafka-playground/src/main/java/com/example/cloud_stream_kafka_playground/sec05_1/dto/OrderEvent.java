package com.example.cloud_stream_kafka_playground.sec05_1.dto;

public record OrderEvent(
        Integer customerId,
        Integer productId,
        OrderType type
) {
}
