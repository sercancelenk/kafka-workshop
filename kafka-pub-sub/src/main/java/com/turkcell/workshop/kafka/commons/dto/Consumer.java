package com.turkcell.workshop.kafka.commons.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class Consumer {
    private String topic;
    private String dataClass;
    private Map<String,Object> props;
    private int concurrency;
    private int retryCount;
    private int timeoutMillis;
    private int stateExpirationMinutes;
    private int syncCommitTimeoutSecond;
    private boolean syncCommit;
    private long backoffIntervalMillis;
}
