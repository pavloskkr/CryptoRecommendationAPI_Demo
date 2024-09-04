package com.xm.cryptorec.cryptorec.service;

import com.xm.cryptorec.cryptorec.model.CryptoData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CsvHandlerServiceTest {

    public CsvHandler csvHandler;

    @BeforeEach
    void setUp() {
        csvHandler = new CsvHandler();
    }

    @Test
    void testParseCsvFiles() throws IOException {
        List<CryptoData> cryptoDataList = csvHandler.parseCsvFiles();

        // make sure list isn't empty and current size
        assertNotNull(cryptoDataList);

        // check first record is the expected
        CryptoData firstRecord = cryptoDataList.get(0);
        assertEquals("BTC", firstRecord.getSymbol());
        assertEquals(1641009600000L, firstRecord.getTimestamp());
        assertEquals(46813.21, firstRecord.getPrice());
    }
}
