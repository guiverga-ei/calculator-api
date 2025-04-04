package com.wit.rest.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class KafkaService {

    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    private static final String REQUEST_TOPIC = "calculator-requests";
    private static final String RESPONSE_TOPIC = "calculator-responses";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String operation, BigDecimal a, BigDecimal b) {
        String id = UUID.randomUUID().toString();
        String message = id + "," + a + "," + b + "," + operation;
        kafkaTemplate.send(REQUEST_TOPIC, id, message);
        log.info("Sent message to Kafka with ID {}: {}", id, message);
    }

    @KafkaListener(topics = RESPONSE_TOPIC)
    public void receiveMessage(String message, org.springframework.messaging.MessageHeaders headers) {
        String key = headers.get("kafka_receivedMessageKey", String.class);
        log.info("Received calculation result for ID {}: {}", key, message);
    }

}
