package com.wit.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class CalculatorController {

    private static final String REQUEST_TOPIC = "calculator_requests";
    private static final String RESPONSE_TOPIC = "calculator_responses";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/sum")
    public void sum(@RequestParam BigDecimal a, @RequestParam BigDecimal b){
        String message = a + "," + b + "sum";
        kafkaTemplate.send(REQUEST_TOPIC, message);
    }

    @GetMapping("/subtraction")
    public void subtraction(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        String message = a + "," + b + ",subtraction";
        kafkaTemplate.send(REQUEST_TOPIC, message);
    }

    @GetMapping("/multiplication")
    public void multiplication(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        String message = a + "," + b + ",multiplication";
        kafkaTemplate.send(REQUEST_TOPIC, message);
    }

    @GetMapping("/division")
    public void division(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        if (b.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        }
        String message = a + "," + b + ",division";
        kafkaTemplate.send(REQUEST_TOPIC, message);
    }

    @KafkaListener(topics = RESPONSE_TOPIC)
    public void receiveResult(String message) {
        System.out.println("Received calculation result: " + message);
    }
    
}
