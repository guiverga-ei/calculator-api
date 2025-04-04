package com.wit.calculator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
public class CalculatorService {

    private  static final MathContext mc = new MathContext(10); // Decimal precision
    private static final String REQUEST_TOPIC = "calculator-requests";
    private static final String RESPONSE_TOPIC = "calculator-responses";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = REQUEST_TOPIC)
    public void processCalculationRequest(String message){
        String[] parts = message.split(",");
        BigDecimal a = new BigDecimal(parts[0]);
        BigDecimal b = new BigDecimal(parts[1]);
        String operation = parts[2];
        BigDecimal result = null;

        switch (operation){
            case "sum":
                result = a.add(b, mc);
                break;
            case "subtraction":
                result = a.subtract(b, mc);
                break;
            case "multiplication":
                result = a.multiply(b, mc);
                break;
            case "division":
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    kafkaTemplate.send(RESPONSE_TOPIC, "Division by zero error");
                    return;
                }
                result = a.divide(b, mc);
                break;
        }

        if (result != null){
            kafkaTemplate.send(RESPONSE_TOPIC, result.toString());
        } else {
            kafkaTemplate.send(RESPONSE_TOPIC, "null");
        }
    }

}
