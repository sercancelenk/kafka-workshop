package com.turkcell.workshop.customer.controller;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class CustomerApiException extends RuntimeException {
    private static final long serialVersionUID = 1;
    private static final ZoneId utc = ZoneId.of("UTC");
    private OffsetDateTime timestamp;
    private HttpStatus status;

    public CustomerApiException(HttpStatus status, String msg) {
        super(msg);
        this.timestamp = OffsetDateTime.now(utc);
        this.status = status;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
