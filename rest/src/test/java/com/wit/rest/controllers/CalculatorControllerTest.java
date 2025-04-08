package com.wit.rest.controllers;

import com.wit.rest.kafka.KafkaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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

    // Replaces the real KafkaService bean with a mock for controlled testing
    @MockBean
    private KafkaService kafkaService;

    /**
     * Executes a GET request asynchronously to the given URI and asserts that:
     * - the request was dispatched asynchronously;
     * - the response has status 200 OK;
     * - the response contains the X-Request-ID header;
     * - the response body matches the expected JSON.
     *
     * @param uri          the URI to perform the GET request on
     * @param expectedJson the expected JSON body in the response
     * @throws Exception in case of test failure or request execution issues
     */
    private void assertAsyncOperation(String uri, String expectedJson) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(uri)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-ID"))
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should return result for valid sum operation")
    void shouldReturnResultForSum() throws Exception {
        when(kafkaService.sendMessage(anyString(), eq("sum"), eq(new BigDecimal("2")), eq(new BigDecimal("3"))))
                .thenReturn(CompletableFuture.completedFuture("5"));

        assertAsyncOperation("/api/sum?a=2&b=3", "{\"result\": 5}");
    }

    @Test
    @DisplayName("Should return result for valid subtraction operation")
    void shouldReturnResultForSubtraction() throws Exception {
        when(kafkaService.sendMessage(anyString(), eq("subtraction"), eq(new BigDecimal("5")), eq(new BigDecimal("3"))))
                .thenReturn(CompletableFuture.completedFuture("2"));

        assertAsyncOperation("/api/subtraction?a=5&b=3", "{\"result\": 2}");
    }

    @Test
    @DisplayName("Should return result for valid multiplication operation")
    void shouldReturnResultForMultiplication() throws Exception {
        when(kafkaService.sendMessage(anyString(), eq("multiplication"), eq(new BigDecimal("4")), eq(new BigDecimal("3"))))
                .thenReturn(CompletableFuture.completedFuture("12"));

        assertAsyncOperation("/api/multiplication?a=4&b=3", "{\"result\": 12}");
    }

    @Test
    @DisplayName("Should return result for valid division operation")
    void shouldReturnResultForDivision() throws Exception {
        when(kafkaService.sendMessage(anyString(), eq("division"), eq(new BigDecimal("10")), eq(new BigDecimal("2"))))
                .thenReturn(CompletableFuture.completedFuture("5"));

        assertAsyncOperation("/api/division?a=10&b=2", "{\"result\": 5}");
    }

    @Test
    @DisplayName("Should return 400 Bad Request when a parameter is missing")
    void shouldReturnBadRequestWhenMissingParameters() throws Exception {
        mockMvc.perform(get("/api/sum")
                        .param("a", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid parameters")
    void shouldReturnBadRequestWhenParametersAreInvalid() throws Exception {
        mockMvc.perform(get("/api/sum")
                        .param("a", "abc")
                        .param("b", "xyz")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error when Kafka fails")
    void shouldReturnErrorWhenKafkaFails() throws Exception {
        when(kafkaService.sendMessage(anyString(), eq("sum"), eq(new BigDecimal("2")), eq(new BigDecimal("3"))))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka error")));

        MvcResult mvcResult = mockMvc.perform(get("/api/sum")
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
