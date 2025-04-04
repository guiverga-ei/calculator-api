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
    private ConcurrentHashMap<String, CompletableFuture<String>> responseFutures = new ConcurrentHashMap<>();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public CompletableFuture<String> sendMessage(String operation, BigDecimal a, BigDecimal b) {
        String id = UUID.randomUUID().toString();
        String message = String.format("%s,%s,%s,%s", id, a, b, operation);
        CompletableFuture<String> future = new CompletableFuture<>();
        responseFutures.put(id, future);
        kafkaTemplate.send(REQUEST_TOPIC, id, message);
        logger.info("Sent message: ID={}, Operation={}", id, operation);
        return future;
    }

    @KafkaListener(topics = RESPONSE_TOPIC)
    public void handleResponse(String message, @Header("kafka_receivedMessageKey") String key) {
        CompletableFuture<String> future = responseFutures.remove(key);
        if (future != null) {
            future.complete(message);
            logger.info("Received response for ID {}: {}", key, message);
        } else {
            logger.warn("No future associated with ID {}", key);
        }
    }
}
