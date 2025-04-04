package com.wit.rest.controllers;

import com.wit.rest.kafka.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class CalculatorController {

    @Autowired
    private KafkaService kafkaService;

    @GetMapping(path = "/{operation}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<?>> performOperation(@PathVariable String operation,
                                                                 @RequestParam BigDecimal a,
                                                                 @RequestParam BigDecimal b) {
        if (operation.equals("division") && b.compareTo(BigDecimal.ZERO) == 0) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"error\": \"Division by zero is not allowed\"}")
            );
        }

        return kafkaService.sendMessage(operation, a, b)
                .thenApply(result -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"result\": " + result + "}"))
                .handle((res, ex) -> {
                    if (ex != null) {
                        return ResponseEntity.internalServerError()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("{\"error\": \"" + ex.getMessage() + "\"}");
                    }
                    return res;
                });
    }
}
