package com.wit.rest.controllers;

import com.wit.rest.kafka.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class CalculatorController {

    @Autowired
    private KafkaService kafkaService;

    @GetMapping("/sum")
    public ResponseEntity<?> sum(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        try {
            kafkaService.sendMessage("sum", a, b);
            return ResponseEntity.ok("Request for sum accepted");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/subtraction")
    public ResponseEntity<?> subtraction(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        try {
            kafkaService.sendMessage("subtraction", a, b);
            return ResponseEntity.ok("Request for subtraction accepted");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/multiplication")
    public ResponseEntity<?> multiplication(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        try {
            kafkaService.sendMessage("multiplication", a, b);
            return ResponseEntity.ok("Request for multiplication accepted");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/division")
    public ResponseEntity<?> division(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        try {
            if (b.compareTo(BigDecimal.ZERO) == 0) {
                throw new ArithmeticException("Division by zero is not allowed");
            }
            kafkaService.sendMessage("division", a, b);
            return ResponseEntity.ok("Request for division accepted");
        } catch (ArithmeticException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing request: " + e.getMessage());
        }
    }
}
