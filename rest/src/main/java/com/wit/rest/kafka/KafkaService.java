package com.wit.rest.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private static final String REQUEST_TOPIC = "calculator-requests";
    private static final String RESPONSE_TOPIC = "calculator-responses";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message, String operation) {
        kafkaTemplate.send(REQUEST_TOPIC, operation, message);
    }

    @KafkaListener(topics = RESPONSE_TOPIC)
    public void receiveMessage(String message) {
        System.out.println("Received calculation result: " + message);
    }

}
