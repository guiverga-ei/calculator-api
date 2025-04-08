package com.wit.calculator.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculatorServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private CalculatorService calculatorService;

    @BeforeEach
    void setup() {
        Mockito.reset(kafkaTemplate);
    }

    @Test
    void testProcessCalculationRequest_validSum() {
        // given
        String requestId = "abc123";
        String message = requestId + ",2,3,sum";

        // when
        calculatorService.processCalculationRequest(message);

        // then
        verify(kafkaTemplate).send("calculator-responses", requestId, "5");
    }

    @Test
    void testProcessCalculationRequest_divisionByZero() {
        // given
        String requestId = "xyz789";
        String message = requestId + ",10,0,division";

        // when
        calculatorService.processCalculationRequest(message);

        // then
        verify(kafkaTemplate).send("calculator-responses", requestId, "Division by zero is not allowed");
    }

    @Test
    void testProcessCalculationRequest_invalidNumberFormat() {
        // given
        String requestId = "badnumber";
        String message = requestId + ",abc,2,sum";

        // when
        calculatorService.processCalculationRequest(message);

        // then
        verify(kafkaTemplate).send("calculator-responses", requestId, "Invalid number format");
    }

    @Test
    void testProcessCalculationRequest_unsupportedOperation() {
        // given
        String requestId = "badop";
        String message = requestId + ",10,2,mod";

        // when
        calculatorService.processCalculationRequest(message);

        // then
        verify(kafkaTemplate).send(eq("calculator-responses"), eq(requestId), contains("Operation not supported"));
    }

    @Test
    void testProcessCalculationRequest_missingId() {
        // given
        String message = " ,10,2,sum"; // empty ID

        // when
        calculatorService.processCalculationRequest(message);

        // then
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void testProcessCalculationRequest_invalidFormat() {
        // given
        String message = "only,three,parts";

        // when
        calculatorService.processCalculationRequest(message);

        // then
        verifyNoInteractions(kafkaTemplate);
    }
}
