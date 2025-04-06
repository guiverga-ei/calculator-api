package com.wit.rest.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);
    private static final String REQUEST_TOPIC = "calculator-requests";
    private static final String RESPONSE_TOPIC = "calculator-responses";
    private final ConcurrentHashMap<String, CompletableFuture<String>> responseFutures = new ConcurrentHashMap<>();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public CompletableFuture<String> sendMessage(String requestId, String operation, BigDecimal a, BigDecimal b) {
        String message = String.format("%s,%s,%s,%s", requestId, a, b, operation);
        CompletableFuture<String> future = new CompletableFuture<>();
        responseFutures.put(requestId, future);
        kafkaTemplate.send(REQUEST_TOPIC, requestId, message);
        logger.info("Sent message: requestId={}, a={}, b={}, Operation='{}'", requestId, a, b, operation);
        return future;
    }

    @KafkaListener(topics = RESPONSE_TOPIC)
    public void handleResponse(String result, @Header("kafka_receivedMessageKey") String requestId) {
        CompletableFuture<String> future = responseFutures.remove(requestId);
        if (future != null) {
            future.complete(result);
            logger.info("Received response for requestId {}: result={}", requestId, result);
        } else {
            logger.warn("No future associated with requestId {}", requestId);
        }
    }
}
