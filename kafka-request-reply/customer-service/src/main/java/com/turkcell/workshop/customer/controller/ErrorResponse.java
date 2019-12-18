package com.turkcell.workshop.customer.controller;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class ErrorResponse {
    @NotNull
    @Valid
    private OffsetDateTime timestamp = null;
    @NotNull
    private Integer status = null;
    private String error = null;
    @NotNull
    @Size(max = 255)
    private String message = null;
    private String path = null;
}
