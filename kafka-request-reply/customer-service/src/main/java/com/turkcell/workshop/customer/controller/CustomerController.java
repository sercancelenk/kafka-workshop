package com.turkcell.workshop.customer.controller;

import com.turkcell.workshop.customer.service.CustomerService;
import com.turkcell.workshop.requestreplycommon.dto.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping(value = "/subscription/{msisdn}",
            produces = {"application/com.turkcell.workshop+json"})
    public DeferredResult<ResponseEntity<Subscription>> getSubscription(@PathVariable("msisdn") String msisdn) {
        DeferredResult<ResponseEntity<Subscription>> result = new DeferredResult<>();
        CompletableFuture<Subscription> reply = customerService.getCustomerSubscriptionAsync(msisdn);
        reply.thenAccept(subscription ->
                result.setResult(new ResponseEntity<>(subscription, HttpStatus.OK))
        ).exceptionally(ex -> {
            result.setErrorResult(new CustomerApiException(HttpStatus.NOT_FOUND, ex.getCause().getMessage()));
            return null;
        });
        return result;
    }


    @ExceptionHandler(CustomerApiException.class)
    public final ResponseEntity<ErrorResponse> handleApiException(CustomerApiException ex,
                                                                  WebRequest request) {
        HttpStatus status = ex.getStatus();
        ErrorResponse.ErrorResponseBuilder errorDetailsBuilder = ErrorResponse.builder().timestamp(ex.getTimestamp())
                .status(status.value()).error(status.getReasonPhrase()).message(ex.getMessage());
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            HttpServletRequest servletRequest =
                    servletWebRequest.getNativeRequest(HttpServletRequest.class);
            if (servletRequest != null) {
                errorDetailsBuilder.path(servletRequest.getRequestURI());
            }
        }
        return new ResponseEntity<>(errorDetailsBuilder.build(), status);
    }
}
