package com.wit.calculator.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
public class CalculatorService {

    private static final Logger log = LoggerFactory.getLogger(CalculatorService.class);
    private static final MathContext mc = new MathContext(10);
    private static final String REQUEST_TOPIC = "calculator-requests";
    private static final String RESPONSE_TOPIC = "calculator-responses";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = REQUEST_TOPIC)
    public void processCalculationRequest(String message) {
        String requestId = null;

        try {
            String[] parts = message.split(",");
            if (parts.length != 4) { // requestId, a, b and operation
                log.error("Invalid message format: expected 4 parts but got {}", parts.length);
                return;
            }

            requestId = parts[0].trim();
            if (requestId.isEmpty()) {
                log.error("Missing ID in request");
                return;
            }
            MDC.put("requestId", requestId);

            BigDecimal a = parseDecimal(parts[1]);
            BigDecimal b = parseDecimal(parts[2]);
            String operation = parts[3].trim().toLowerCase();

            log.info("Processing message requestId: {}, a={}, b={}, operation='{}'", requestId, a, b, operation);

            BigDecimal result = performOperation(a, b, operation);

            kafkaTemplate.send(RESPONSE_TOPIC, requestId, result.toString());
            log.info("Operation '{}' completed successfully for requestId {}, result={}", operation, requestId, result);

        } catch (ArithmeticException e) {
            log.warn("Arithmetic error: {}", e.getMessage());
            sendErrorResponse(requestId, "Division by zero is not allowed");

        } catch (NumberFormatException e) {
            log.error("Invalid number format: {}", e.getMessage());
            sendErrorResponse(requestId, "Invalid number format");

        } catch (IllegalArgumentException e) {
            log.warn("Unsupported operation: {}", e.getMessage());
            sendErrorResponse(requestId, e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error", e);
            sendErrorResponse(requestId, "Error in processing request");
        } finally {
            MDC.clear();
        }
    }

    private BigDecimal parseDecimal(String value) {
        return new BigDecimal(value.trim());
    }

    BigDecimal performOperation(BigDecimal a, BigDecimal b, String operation) {
        return switch (operation) {
            case "sum" -> a.add(b, mc);
            case "subtraction" -> a.subtract(b, mc);
            case "multiplication" -> a.multiply(b, mc);
            case "division" -> {
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Division by zero is not allowed");
                }
                yield a.divide(b, mc);
            }
            default -> throw new IllegalArgumentException("Operation not supported: " + operation);
        };
    }

    private void sendErrorResponse(String requestId, String message) {
        if (requestId != null && !requestId.isBlank()) {
            kafkaTemplate.send(RESPONSE_TOPIC, requestId, message);
        }
    }
}
