package com.wit.calculator.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
public class CalculatorService {

    private static final Logger log = LoggerFactory.getLogger(CalculatorService.class);
    private static final MathContext mc = new MathContext(10); // Decimal precision
    private static final String REQUEST_TOPIC = "calculator-requests";
    private static final String RESPONSE_TOPIC = "calculator-responses";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = REQUEST_TOPIC)
    public void processCalculationRequest(String message) {
        log.debug("Processing message: {}", message);
        try {
            String[] parts = message.split(",");
            if (parts.length != 4) {  // Expecting 4 parts: ID, operand a, operand b, operation
                log.error("Invalid message format: {}", message);
                return;
            }
            String id = parts[0];  // Extract the unique identifier for the request
            BigDecimal a = new BigDecimal(parts[1]);
            BigDecimal b = new BigDecimal(parts[2]);
            String operation = parts[3];
            BigDecimal result = performOperation(a, b, operation);
            if (result != null) {
                kafkaTemplate.send(RESPONSE_TOPIC, id, result.toString());  // Send response with the same ID
                log.info("Operation {} completed successfully for ID {}", operation, id);
            } else {
                kafkaTemplate.send(RESPONSE_TOPIC, id, "Operation not supported");
                log.warn("Operation not supported: {}", operation);
            }
        } catch (NumberFormatException e) {
            log.error("Number format exception: {}", e.getMessage());
            kafkaTemplate.send(RESPONSE_TOPIC, "Invalid number format");
        } catch (Exception e) {
            log.error("Error processing calculation request", e);
            kafkaTemplate.send(RESPONSE_TOPIC, "Error in processing request");
        }
    }

    private BigDecimal performOperation(BigDecimal a, BigDecimal b, String operation) {
        switch (operation) {
            case "sum":
                return a.add(b, mc);
            case "subtraction":
                return a.subtract(b, mc);
            case "multiplication":
                return a.multiply(b, mc);
            case "division":
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Division by zero is not allowed");
                }
                return a.divide(b, mc);
            default:
                return null;
        }
    }

}
