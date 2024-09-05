package com.xm.cryptorec.cryptorec.service;

import com.xm.cryptorec.cryptorec.dto.CryptoNormalizedRangeDto;
import com.xm.cryptorec.cryptorec.model.CryptoData;
import com.xm.cryptorec.cryptorec.model.CryptoStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@EnableCaching
public class CryptoComputationServiceTest {

    @Mock
    private CsvHandler csvHandler;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private CryptoComputationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testComputeStats() throws Exception {
        // some mock data
        List<CryptoData> cryptoDataList = List.of(
                new CryptoData(1641009600000L, "BTC", 46813.21),
                new CryptoData(1641009700000L, "BTC", 47000.00),
                new CryptoData(1641009800000L, "BTC", 46000.50),
                new CryptoData(1641009900000L, "BTC", 48000.00)
        );

        // csv parsing sim
        when(csvHandler.parseCsvFiles()).thenReturn(cryptoDataList);

        // compute stats
        CryptoStats stats = service.computeStats("BTC");

        // output validation
        assertEquals(46000.50, stats.getMinPrice(), 0.01);
        assertEquals(48000.00, stats.getMaxPrice(), 0.01);
        assertEquals(46813.21, stats.getOldestPrice(), 0.01);
        assertEquals(48000.00, stats.getNewestPrice(), 0.01);
        assertEquals((48000.00 - 46000.50) / 46000.50, stats.getNormalizedRange(), 0.0001);

        // verify that CSV parsing was called once
        verify(csvHandler, times(1)).parseCsvFiles();
    }

    @Test
    void testGetAllCryptosSortedByNormalizedRange() throws Exception {
        // some mock data
        List<CryptoData> cryptoDataList = List.of(
                new CryptoData(1641009600000L, "BTC", 46813.21),
                new CryptoData(1641009700000L, "ETH", 3500.00),
                new CryptoData(1641009800000L, "BTC", 47000.00),
                new CryptoData(1641009900000L, "ETH", 3600.00)
        );

        // mock csv parsing sim
        when(csvHandler.parseCsvFiles()).thenReturn(cryptoDataList);

        // service method call
        List<CryptoNormalizedRangeDto> result = service.getAllCryptosSortedByNormalizedRange();

        // assert result size
        assertEquals(2, result.size());

        // assert result order
        assertEquals("ETH", result.get(0).getCrypto());
        assertEquals("BTC", result.get(1).getCrypto());

        // assert normalized ranges
        assertEquals((3600.00 - 3500.00) / 3500.00, result.get(0).getNormalizedRange(), 0.0001);
        assertEquals((47000.00 - 46813.21) / 46813.21, result.get(1).getNormalizedRange(), 0.0001);

        // verify csv parsing has been called at least once
        verify(csvHandler, atLeastOnce()).parseCsvFiles();

    }

    @Test
    void testGetHighestNormalizedRangeForDate() throws Exception {
        // some mock data within close date range
        List<CryptoData> cryptoDataList = List.of(
                new CryptoData(1641009600000L, "BTC", 46813.21),
                new CryptoData(1641019600000L, "ETH", 3500.00),
                new CryptoData(1641019900000L, "BTC", 47000.00),
                new CryptoData(1641020000000L, "ETH", 3600.00)
        );

        // mock csv parsing sim
        when(csvHandler.parseCsvFiles()).thenReturn(cryptoDataList);

        // define a date within close date range
        String date = "2022-01-01";

        // service method call
        CryptoStats result = service.getHighestNormalizedRangeForDate(date);

        // assert result
        assertEquals("ETH", result.getSymbol());
        assertEquals((3600.00 - 3500.00) / 3500.00, result.getNormalizedRange(), 0.0001);

        // verify csv parsing has been called at least once
        verify(csvHandler, atLeastOnce()).parseCsvFiles();
    }

    @Test
    void testGetHighestNormalizedRangeForDate_NoData() throws Exception {
        // some mock data with outside date range
        List<CryptoData> cryptoDataList = List.of(
                new CryptoData(1631009600000L, "BTC", 46813.21),
                new CryptoData(1631019600000L, "ETH", 3500.00)
        );

        // mock csv parsing sim
        when(csvHandler.parseCsvFiles()).thenReturn(cryptoDataList);

        // define a date outside close date range
        String date = "2022-01-01";

        // define an exception variable to store the thrown exceptions
        Exception exception = null;
        try {
            service.getHighestNormalizedRangeForDate(date);
        } catch (Exception e) {
            exception = e;
        }

        // Assert that an exception was thrown
        assertNotNull(exception);
        assertEquals("No data found for date: " + date, exception.getMessage());

        // Verify that CSV parsing was called once
        verify(csvHandler, times(1)).parseCsvFiles();
    }
}
