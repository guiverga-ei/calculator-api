package com.wit.rest.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for asynchronously communicating with the calculator module via Kafka.
 * It sends arithmetic requests and listens for corresponding responses using request correlation.
 */
@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    // Kafka topic names for sending requests and receiving responses
    private static final String REQUEST_TOPIC = "calculator-requests";
    private static final String RESPONSE_TOPIC = "calculator-responses";

    // In-memory store for mapping requestId to their corresponding CompletableFuture awaiting response
    private final ConcurrentHashMap<String, CompletableFuture<String>> responseFutures = new ConcurrentHashMap<>();

    private final KafkaTemplate<String, String> kafkaTemplate;
    /**
     * Constructor-based dependency injection for KafkaTemplate.
     * Promotes immutability and simplifies testing.
     *
     * @param kafkaTemplate Kafka template used to publish messages to Kafka topics
     */
    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a message to the calculator request topic.
     * Constructs a message in the format: requestId,a,b,operation
     * Stores a CompletableFuture to be completed when a response is received.
     *
     * @param requestId Unique identifier for the request
     * @param operation The operation to be performed (sum, subtraction, etc.)
     * @param a         First operand
     * @param b         Second operand
     * @return CompletableFuture that will be completed with the result
     */
    public CompletableFuture<String> sendMessage(String requestId, String operation, BigDecimal a, BigDecimal b) {
        String message = String.format("%s,%s,%s,%s", requestId, a, b, operation);
        CompletableFuture<String> future = new CompletableFuture<>();
        responseFutures.put(requestId, future);
        kafkaTemplate.send(REQUEST_TOPIC, requestId, message);
        logger.info("Sent message: requestId={}, a={}, b={}, Operation='{}'", requestId, a, b, operation);
        return future;
    }

    /**
     * Kafka listener that handles responses coming from the calculator service.
     * Completes the matching CompletableFuture to unblock the controller waiting for the result.
     *
     * @param result     The result received from the calculator service
     * @param requestId  The requestId associated with the response (from Kafka message key)
     */
    @KafkaListener(topics = RESPONSE_TOPIC)
    public void handleResponse(String result, @Header("kafka_receivedMessageKey") String requestId) {
        CompletableFuture<String> future = responseFutures.remove(requestId); // Retrieve and remove the future
        if (future != null) {
            future.complete(result);
            logger.info("Received response for requestId {}: result={}", requestId, result);
        } else {
            logger.warn("No future associated with requestId {}", requestId);
        }
    }
}
