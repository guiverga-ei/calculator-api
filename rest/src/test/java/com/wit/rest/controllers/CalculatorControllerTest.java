package com.wit.rest.controllers;

import com.wit.rest.kafka.KafkaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaService kafkaService;

    @Test
    void shouldReturnResultForSum() throws Exception {
        when(kafkaService.sendMessage(anyString(), eq("sum"), eq(new BigDecimal("2")), eq(new BigDecimal("3"))))
                .thenReturn(CompletableFuture.completedFuture("5"));

        var mvcResult = mockMvc.perform(get("/api/sum")
                        .param("a", "2")
                        .param("b", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-ID"))
                .andExpect(content().json("{\"result\": 5}"));
    }

    @Test
    void shouldReturnResultForSubtraction() throws Exception {
        when(kafkaService.sendMessage(anyString(), eq("subtraction"), eq(new BigDecimal("5")), eq(new BigDecimal("3"))))
                .thenReturn(CompletableFuture.completedFuture("2"));

        var mvcResult = mockMvc.perform(get("/api/subtraction")
                        .param("a", "5")
                        .param("b", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-ID"))
                .andExpect(content().json("{\"result\": 2}"));
    }

    @Test
    void shouldReturnResultForMultiplication() throws Exception {
        when(kafkaService.sendMessage(anyString(), eq("multiplication"), eq(new BigDecimal("4")), eq(new BigDecimal("3"))))
                .thenReturn(CompletableFuture.completedFuture("12"));

        var mvcResult = mockMvc.perform(get("/api/multiplication")
                        .param("a", "4")
                        .param("b", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-ID"))
                .andExpect(content().json("{\"result\": 12}"));
    }

    @Test
    void shouldReturnResultForDivision() throws Exception {
        when(kafkaService.sendMessage(anyString(), eq("division"), eq(new BigDecimal("10")), eq(new BigDecimal("2"))))
                .thenReturn(CompletableFuture.completedFuture("5"));

        var mvcResult = mockMvc.perform(get("/api/division")
                        .param("a", "10")
                        .param("b", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-ID"))
                .andExpect(content().json("{\"result\": 5}"));
    }

    @Test
    void shouldReturnBadRequestWhenMissingParameters() throws Exception {
        mockMvc.perform(get("/api/sum")
                        .param("a", "2")
                        // falta o parâmetro 'b'
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenParametersAreInvalid() throws Exception {
        mockMvc.perform(get("/api/sum")
                        .param("a", "abc")
                        .param("b", "xyz")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnErrorWhenKafkaFails() throws Exception {
        when(kafkaService.sendMessage(anyString(), eq("sum"), eq(new BigDecimal("2")), eq(new BigDecimal("3"))))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka error")));

        var mvcResult = mockMvc.perform(get("/api/sum")
                        .param("a", "2")
                        .param("b", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("X-Request-ID"))
                .andExpect(content().json("{\"error\": \"Kafka error\"}"));
    }

}
