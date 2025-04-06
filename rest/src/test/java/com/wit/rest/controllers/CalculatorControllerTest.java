package com.wit.rest.controllers;

import com.wit.rest.kafka.KafkaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaService kafkaService;

    @Test
    void shouldReturnResultForSum() throws Exception {
        Mockito.when(kafkaService.sendMessage("aaa", "sum", new BigDecimal("2"), new BigDecimal("3")))
                .thenReturn(CompletableFuture.completedFuture("5"));

        mockMvc.perform(get("/api/sum?a=2&b=3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"result\": 5}"));
    }

    @Test
    void shouldHandleDivisionByZero() throws Exception {
        mockMvc.perform(get("/api/division?a=5&b=0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Division by zero is not allowed\"}"));
    }
}
