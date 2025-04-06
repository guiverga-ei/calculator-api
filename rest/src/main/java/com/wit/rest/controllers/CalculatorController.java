package com.wit.rest.controllers;

import com.wit.rest.kafka.KafkaService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class CalculatorController {

    @Autowired
    private KafkaService kafkaService;

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

    private CompletableFuture<ResponseEntity<?>> handleOperation(String operation, BigDecimal a, BigDecimal b) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        return kafkaService.sendMessage(requestId, operation, a, b)
                .handle((result, ex) -> {
                    MDC.clear();
                    if (ex != null) {
                        return ResponseEntity.internalServerError()
                                .header("X-Request-ID", requestId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("{\"error\": \"" + ex.getMessage() + "\"}");
                    }
                    return ResponseEntity.ok()
                            .header("X-Request-ID", requestId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"result\": " + result + "}");
                });
    }

}

