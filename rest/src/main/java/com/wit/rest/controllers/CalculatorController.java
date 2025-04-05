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
        if (b.compareTo(BigDecimal.ZERO) == 0) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"error\": \"Division by zero is not allowed\"}")
            );
        }
        return handleOperation("division", a, b);
    }

    private CompletableFuture<ResponseEntity<?>> handleOperation(String operation, BigDecimal a, BigDecimal b) {
        return kafkaService.sendMessage(operation, a, b)
                .handle((result, ex) -> {
                    if (ex != null) {
                        return ResponseEntity.internalServerError()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("{\"error\": \"" + ex.getMessage() + "\"}");
                    }
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"result\": " + result + "}");
                });
    }

}

//    @GetMapping(path = "/{operation}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public CompletableFuture<ResponseEntity<?>> performOperation(@PathVariable String operation,
//                                                                 @RequestParam BigDecimal a,
//                                                                 @RequestParam BigDecimal b) {
//        if (operation.equals("division") && b.compareTo(BigDecimal.ZERO) == 0) {
//            return CompletableFuture.completedFuture(
//                    ResponseEntity.badRequest()
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .body("{\"error\": \"Division by zero is not allowed\"}")
//            );
//        }
//
//        return kafkaService.sendMessage(operation, a, b)
//                .thenApply(result -> ResponseEntity.ok()
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body("{\"result\": " + result + "}"))
//                .handle((res, ex) -> {
//                    if (ex != null) {
//                        return ResponseEntity.internalServerError()
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .body("{\"error\": \"" + ex.getMessage() + "\"}");
//                    }
//                    return res;
//                });
//    }