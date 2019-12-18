package com.turkcell.workshop.requestreplycommon.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Subscription {
    private String offerId;
    private String msisdn;
    private String status;
}
