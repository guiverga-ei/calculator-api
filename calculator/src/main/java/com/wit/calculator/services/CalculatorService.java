package com.wit.calculator.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Service responsible for processing arithmetic operations received from a Kafka topic.
 * It listens to incoming requests, performs the specified calculation, and sends the result back to a response topic.
 */
@Service
public class CalculatorService {

    private static final Logger log = LoggerFactory.getLogger(CalculatorService.class);

    // Defines the precision context (10 digits) for all arithmetic operations to ensure consistency
    private static final MathContext mc = new MathContext(10);

    // Kafka topic names
    private static final String REQUEST_TOPIC = "calculator-requests";
    private static final String RESPONSE_TOPIC = "calculator-responses";

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Constructor-based dependency injection for KafkaTemplate.
     * Promotes immutability and simplifies testing.
     */
    public CalculatorService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Kafka listener method that handles incoming calculation requests.
     * The expected message format is: "requestId,a,b,operation".
     *
     * @param message the message received from the Kafka topic
     */
    @KafkaListener(topics = REQUEST_TOPIC)
    public void processCalculationRequest(String message) {
        String requestId = null;

        try {
            String[] parts = message.split(",");
            if (parts.length != 4) {
                log.error("Invalid message format: expected 4 parts but got {}", parts.length);
                return;
            }

            requestId = parts[0].trim();
            if (requestId.isEmpty()) {
                log.error("Missing requestId in request");
                return;
            }
            MDC.put("requestId", requestId); // Add requestId to logging context for traceability

            BigDecimal a = parseDecimal(parts[1]);
            BigDecimal b = parseDecimal(parts[2]);
            String operation = parts[3].trim().toLowerCase();

            log.info("Processing message requestId: {}, a={}, b={}, operation='{}'", requestId, a, b, operation);

            BigDecimal result = performOperation(a, b, operation);

            // Send the result to the response topic
            kafkaTemplate.send(RESPONSE_TOPIC, requestId, result.toString());
            log.info("Operation '{}' completed successfully for requestId {}, result={}", operation, requestId, result);

        } catch (ArithmeticException e) {
            log.warn("Arithmetic error: {}", e.getMessage());
            sendErrorResponse(requestId, "Division by zero is not allowed");

        } catch (NumberFormatException e) {
            log.warn("Invalid number format: {}", e.getMessage());
            sendErrorResponse(requestId, "Invalid number format");

        } catch (IllegalArgumentException e) {
            log.warn("Unsupported operation: {}", e.getMessage());
            sendErrorResponse(requestId, e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error", e);
            sendErrorResponse(requestId, "Error in processing request");
        } finally {
            MDC.clear(); // Clean up MDC context to avoid leaking values across threads
        }
    }

    /**
     * Parses a string into a BigDecimal, trimming any whitespace.
     *
     * @param value the string to parse
     * @return the parsed BigDecimal
     */
    private BigDecimal parseDecimal(String value) {
        return new BigDecimal(value.trim());
    }

    /**
     * Executes the specified arithmetic operation on two numbers.
     *
     * @param a         the first operand
     * @param b         the second operand
     * @param operation the operation to perform (sum, subtraction, multiplication, division)
     * @return the result of the operation
     * @throws IllegalArgumentException if the operation is not supported
     */
    // Package-private for unit testing purposes (CalculatorServiceTest)
    BigDecimal performOperation(BigDecimal a, BigDecimal b, String operation) {
        return switch (operation) {
            case "sum" -> a.add(b, mc);
            case "subtraction" -> a.subtract(b, mc);
            case "multiplication" -> a.multiply(b, mc);
            case "division" -> {
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Division by zero is not allowed");
                }
                yield a.divide(b, mc); // Performs division with defined precision; throws ArithmeticException if divisor is zero
            }
            default -> throw new IllegalArgumentException("Operation not supported: " + operation);
        };
    }

    /**
     * Sends an error message to the Kafka response topic if the requestId is valid.
     *
     * @param requestId the request identifier
     * @param message   the error message to send
     */
    private void sendErrorResponse(String requestId, String message) {
        if (requestId != null && !requestId.isBlank()) {
            kafkaTemplate.send(RESPONSE_TOPIC, requestId, message);
        }
    }
}
