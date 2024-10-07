package com.example.common.util;

import reactor.kafka.receiver.ReceiverOffset;

public record MessageRecord<T>(
        String key,
        T message,
        ReceiverOffset acknowledgement
) {
}
