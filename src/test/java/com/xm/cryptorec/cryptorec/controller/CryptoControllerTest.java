package com.xm.cryptorec.cryptorec.controller;

import com.xm.cryptorec.cryptorec.dto.CryptoNormalizedRangeDto;
import com.xm.cryptorec.cryptorec.exception.CryptoNotFoundException;
import com.xm.cryptorec.cryptorec.model.CryptoStats;
import com.xm.cryptorec.cryptorec.service.CryptoComputationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@WebMvcTest(CryptoController.class)
public class CryptoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CryptoComputationService service;

    @Test
    void testGetCryptosSortedByNormalizedRange() throws Exception {
        // some mock data for testing
        List<CryptoNormalizedRangeDto> mockData = List.of(
                new CryptoNormalizedRangeDto("BTC", 0.05),
                new CryptoNormalizedRangeDto("ETH", 0.03)
        );
        when(service.getAllCryptosSortedByNormalizedRange()).thenReturn(mockData);

        // Perform GET request and verify the result
        mockMvc.perform(get("/api/cryptos/normalized-range")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].crypto", is("BTC")))
                .andExpect(jsonPath("$[0].normalizedRange", is(0.05)))
                .andExpect(jsonPath("$[1].crypto", is("ETH")))
                .andExpect(jsonPath("$[1].normalizedRange", is(0.03)));
    }

    @Test
    void testGetCryptoStats() throws Exception {
        // some mock data for testing
        CryptoStats mockStats = new CryptoStats("BTC", 46813.21, 48000.00, 48000.00, 46000.50, 0.05);

        // service mocking
        when(service.computeStats("BTC")).thenReturn(mockStats);

        // Perform GET request and verify the result
        mockMvc.perform(get("/api/cryptos/BTC/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol", is("BTC")))
                .andExpect(jsonPath("$.oldestPrice", is(46813.21)))
                .andExpect(jsonPath("$.newestPrice", is(48000.00)))
                .andExpect(jsonPath("$.maxPrice", is(48000.00)))
                .andExpect(jsonPath("$.minPrice", is(46000.50)))
                .andExpect(jsonPath("$.normalizedRange", is(0.05)));
    }

    @Test
    void testGetCryptoWithHighestNormalizedRange() throws Exception {
        // some mock data for testing
        CryptoStats mockStats = new CryptoStats("BTC", 46813.21, 48000.00, 48000.00, 46000.50, 0.05);

        // service mocking
        when(service.getHighestNormalizedRangeForDate("2023-08-15")).thenReturn(mockStats);

        // Perform GET request and validate the result
        mockMvc.perform(get("/api/cryptos/normalized-range/2023-08-15")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crypto", is("BTC")))
                .andExpect(jsonPath("$.normalizedRange", is(0.05)));
    }

    @Test
    void testGetCryptoStats_NotFound() throws Exception {
        // Simulate exception thrown by the service
        when(service.computeStats("BTC")).thenThrow(new CryptoNotFoundException("BTC", "Data for symbol BTC not found"));

        // Perform GET request and verify exception handling
        mockMvc.perform(get("/api/cryptos/BTC/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("Crypto: BTC not found")))
                .andExpect(jsonPath("$.message", containsString("Data for symbol BTC not found")));
    }
}
