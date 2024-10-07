package com.example.common.util;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import reactor.kafka.receiver.ReceiverOffset;

import java.lang.Record;

public class MessageConverter {
    public static <T> MessageRecord<T> toRecord(Message<T> message) {
        var payload = message.getPayload();
        var key = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class);
        var ack = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class);
        return new MessageRecord<>(key, payload, ack);
    }
}
