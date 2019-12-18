package com.turkcell.workshop.kafka.commons.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Producer {
    private Map<String,Object> props;
}