package com.example.cloud_stream_kafka_playground.sec07.dto;

public record OrderEvent(
        Integer customerId,
        Integer productId,
        OrderType type
) {
}
