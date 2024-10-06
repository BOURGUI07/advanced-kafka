package com.example.cloud_stream_kafka_playground.sec11.dto;

import java.io.Serializable;

public record Phone(int phone) implements ContactMethod {
    @Override
    public void contact() {
        System.out.println("CONTACTING VIA: " + phone);
    }
}
