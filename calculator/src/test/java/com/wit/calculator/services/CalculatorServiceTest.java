package com.wit.calculator.services;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorServiceTest {

    private final CalculatorService calculatorService = new CalculatorService();

    @Test
    void shouldAddTwoNumbers() {
        BigDecimal result = calculatorService.performOperation(new BigDecimal("2"), new BigDecimal("3"), "sum");
        assertThat(result).isEqualByComparingTo(new BigDecimal("5"));
    }

    @Test
    void shouldSubtractTwoNumbers() {
        BigDecimal result = calculatorService.performOperation(new BigDecimal("5"), new BigDecimal("3"), "subtraction");
        assertThat(result).isEqualByComparingTo(new BigDecimal("2"));
    }

    @Test
    void shouldMultiplyTwoNumbers() {
        BigDecimal result = calculatorService.performOperation(new BigDecimal("4"), new BigDecimal("2.5"), "multiplication");
        assertThat(result).isEqualByComparingTo(new BigDecimal("10.0"));
    }

    @Test
    void shouldDivideTwoNumbers() {
        BigDecimal result = calculatorService.performOperation(new BigDecimal("10"), new BigDecimal("2"), "division");
        assertThat(result).isEqualByComparingTo(new BigDecimal("5.0"));
    }

    @Test
    void shouldThrowOnDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> {
            calculatorService.performOperation(new BigDecimal("10"), BigDecimal.ZERO, "division");
        });
    }

    @Test
    void shouldReturnNullOnInvalidOperation() {
        BigDecimal result = calculatorService.performOperation(new BigDecimal("2"), new BigDecimal("3"), "invalid");
        assertThat(result).isNull();
    }
}
