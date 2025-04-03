package com.wit.calculator.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
public class CalculatorService {

    private  static final MathContext mc = new MathContext(10); // Decimal precision

    public BigDecimal add(BigDecimal a, BigDecimal b){
        return a.add(b, mc);
    }

    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b, mc);
    }

    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b, mc);
    }

    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (b.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        }
        return a.divide(b, mc);
    }

}
