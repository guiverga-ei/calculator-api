package com.wit.rest.controllers;

import com.wit.rest.kafka.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class CalculatorController {

    @Autowired
    private KafkaService kafkaService;

    @GetMapping("/sum")
    public void sum(@RequestParam BigDecimal a, @RequestParam BigDecimal b){
        String message = a + "," + b + ",sum";
        kafkaService.sendMessage(message, "sum");
    }

    @GetMapping("/subtraction")
    public void subtraction(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        String message = a + "," + b + ",subtraction";
        kafkaService.sendMessage(message, "subtraction");
    }

    @GetMapping("/multiplication")
    public void multiplication(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        String message = a + "," + b + ",multiplication";
        kafkaService.sendMessage(message, "multiplication");
    }

    @GetMapping("/division")
    public void division(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        if (b.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        }
        String message = a + "," + b + ",division";
        kafkaService.sendMessage(message, "division");
    }

}
