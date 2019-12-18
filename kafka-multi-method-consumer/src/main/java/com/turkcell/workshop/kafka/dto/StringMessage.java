package com.turkcell.workshop.kafka.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class StringMessage {
    private String message;
}