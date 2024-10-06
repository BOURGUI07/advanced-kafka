package com.example.cloud_stream_kafka_playground.sec09.dto;

public record PhysicalDelivery(
        Integer productId,
        String street,
        String city,
        String country
) {
}
