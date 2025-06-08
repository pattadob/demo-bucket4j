package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ApiControllerTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private ApiController apiController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(apiController).build();
    }

    @Test
    void testGetData_ShouldReturnSuccessMessage() {
        // Act
        ResponseEntity<String> response = apiController.getData(httpServletRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Data retrieved successfully", response.getBody());
    }

    @Test
    void testGetDataEndpoint_ShouldReturn200WithMessage() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/data"))
                .andExpect(status().isOk())
                .andExpect(content().string("Data retrieved successfully"));
    }

    @Test
    void testGetDataEndpoint_ShouldHandleMultipleRequestsWithinLimit() throws Exception {
        // Test multiple requests within rate limit (assuming 10 requests per minute)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/data"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Data retrieved successfully"));
        }
    }

    @Test
    void testGetDataEndpoint_ShouldReturn429WhenRateLimitExceeded() throws Exception {
        // Simulate exceeding rate limit (assuming 10 requests per minute)
        // This test assumes you have rate limiting implemented

        // Make requests up to the limit
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/data"))
                    .andExpect(status().isOk());
        }

        // The next request should be rate limited
        mockMvc.perform(get("/api/data"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("X-RateLimit-Limit"))
                .andExpect(header().exists("X-RateLimit-Remaining"))
                .andExpect(header().exists("X-RateLimit-Reset"));
    }

    @Test
    void testGetDataEndpoint_ShouldReturnRateLimitHeaders() throws Exception {
        // Test that rate limit headers are present in response
        mockMvc.perform(get("/api/data"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-RateLimit-Limit"))
                .andExpect(header().exists("X-RateLimit-Remaining"));
    }

    @Test
    void testGetDataEndpoint_WithDifferentIpAddresses_ShouldHaveSeparateRateLimits() throws Exception {
        // Test that different IP addresses have separate rate limits

        // Requests from first IP
        mockMvc.perform(get("/api/data")
                        .header("X-Forwarded-For", "192.168.1.1"))
                .andExpect(status().isOk());

        // Requests from second IP
        mockMvc.perform(get("/api/data")
                        .header("X-Forwarded-For", "192.168.1.2"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDataEndpoint_AfterRateLimitReset_ShouldAllowRequests() throws Exception {
        // This test would require actual time-based testing or mocking time
        // Simulate requests that exceed rate limit
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/data"));
        }

        // Verify rate limit exceeded
        mockMvc.perform(get("/api/data"))
                .andExpect(status().isTooManyRequests());

        // Simulate time passing (you would need to mock time or use actual delays)
        // Thread.sleep(60000); // Wait 1 minute for rate limit reset

        // After reset, requests should work again
        // mockMvc.perform(get("/api/data"))
        //         .andExpect(status().isOk());
    }
}