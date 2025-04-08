package com.wit.rest.controllers;

import com.wit.rest.kafka.KafkaService;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller that exposes endpoints for basic arithmetic operations.
 * Sends calculation requests to the backend via Kafka and returns asynchronous responses.
 */
@RestController
@RequestMapping("/api")
public class CalculatorController {

    private final KafkaService kafkaService;

    public CalculatorController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @GetMapping(value = "/sum", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<?>> sum(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return handleOperation("sum", a, b);
    }

    @GetMapping(value = "/subtraction", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<?>> subtraction(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return handleOperation("subtraction", a, b);
    }

    @GetMapping(value = "/multiplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<?>> multiplication(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return handleOperation("multiplication", a, b);
    }

    @GetMapping(value = "/division", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<?>> division(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return handleOperation("division", a, b);
    }

    /**
     * Handles an arithmetic operation by generating a requestId and sending the request via Kafka.
     * Returns the result asynchronously once the response is received.
     *
     * @param operation the operation to perform ("sum", "subtraction", "multiplication", "division")
     * @param a the first operand
     * @param b the second operand
     * @return a CompletableFuture containing the HTTP response with the result or error
     */
    private CompletableFuture<ResponseEntity<?>> handleOperation(String operation, BigDecimal a, BigDecimal b) {
        String requestId = UUID.randomUUID().toString(); // Generate a unique requestId for logging and tracking
        MDC.put("requestId", requestId); // Add the requestId to the logging context (for tracing logs)
        return kafkaService.sendMessage(requestId, operation, a, b)
                .handle((result, ex) -> {
                    MDC.clear(); // To prevent leakage between threads
                    if (ex != null) {
                        return buildResponse(requestId, "{\"error\": \"" + ex.getMessage() + "\"}", true);
                    }
                    return buildResponse(requestId, "{\"result\": " + result + "}", false);
                });
    }

    /**
     * Helper method to build a consistent JSON response with appropriate headers and status.
     *
     * @param requestId the unique request identifier
     * @param body the JSON-formatted response body
     * @param isError true if response is an error, false if success
     * @return ResponseEntity with headers and JSON body
     */
    private ResponseEntity<String> buildResponse(String requestId, String body, boolean isError) {
        var builder = isError ? ResponseEntity.internalServerError() : ResponseEntity.ok();
        return builder
                .header("X-Request-ID", requestId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

}

