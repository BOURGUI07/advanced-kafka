package com.example.common.exception;

public class EventAlreadyProcessedException extends RuntimeException {
    private static final String MESSAGE = "THE EVENT IS ALREADY PROCESSED";

    public EventAlreadyProcessedException() {
        super(MESSAGE);
    }
}
