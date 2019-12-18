package com.turkcell.workshop.kafka.commons.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class BatchRequest {
    private List<String> messages;
}
