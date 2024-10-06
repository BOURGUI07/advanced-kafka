package com.example.cloud_stream_kafka_playground.sec11.dto;

public record Email(String email) implements ContactMethod {
    @Override
    public void contact() {
        System.out.println("CONTACTING VIA: " + email);
    }
}
