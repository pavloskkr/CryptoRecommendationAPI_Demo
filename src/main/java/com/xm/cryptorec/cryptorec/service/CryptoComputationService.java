package com.xm.cryptorec.cryptorec.service;

import com.xm.cryptorec.cryptorec.dto.CryptoNormalizedRangeDto;
import com.xm.cryptorec.cryptorec.exception.CryptoNotFoundException;
import com.xm.cryptorec.cryptorec.model.CryptoData;
import com.xm.cryptorec.cryptorec.model.CryptoStats;
import org.apache.commons.codec.digest.Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CryptoComputationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CsvHandler csvHandler;

    @Autowired
    public CryptoComputationService(RedisTemplate<String, Object> redisTemplate, CsvHandler csvHandler) {
        this.redisTemplate = redisTemplate;
        this.csvHandler = csvHandler;
    }

    public CryptoStats computeStats(String symbol) throws Exception {
        // fetch data using csvHandler
        List<CryptoData> data = csvHandler.parseCsvFiles();

        // filter data by symbol
        List<CryptoData> filteredData = data.stream()
                .filter(d -> d.getSymbol().equals(symbol))
                .sorted(Comparator.comparing(CryptoData::getTimestamp))
                .toList();

        if (filteredData.isEmpty()) {
            throw
                    new CryptoNotFoundException(symbol, "Data for " + symbol + " not found");
        }

        // compute stats
        double oldestPrice = filteredData.get(0).getPrice();
        double newestPrice = filteredData.get(filteredData.size() - 1).getPrice();
        double maxPrice = filteredData.stream().mapToDouble(CryptoData::getPrice).max().getAsDouble();
        double minPrice = filteredData.stream().mapToDouble(CryptoData::getPrice).min().getAsDouble();
        double normalizedRange = (maxPrice - minPrice) / minPrice;

        // store stats in redis
        String cacheKey = "crypto:" + symbol;
        CryptoStats stats = new CryptoStats(symbol, oldestPrice, newestPrice, maxPrice, minPrice, normalizedRange);
        redisTemplate.opsForValue().set(cacheKey, stats);

        return stats;
    }

    public CryptoStats getCachedStats(String symbol) {
        return (CryptoStats) redisTemplate.opsForValue().get("crypto:" + symbol);
    }

    public List<CryptoNormalizedRangeDto> getAllCryptosSortedByNormalizedRange() {
        List<CryptoData> data = null;
        try {
            data = csvHandler.parseCsvFiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return data.stream()
                .collect(Collectors.groupingBy(CryptoData::getSymbol))
                .keySet().stream()
                .map(cryptoData -> {
                    try {
                        CryptoStats stats = computeStats(cryptoData);
                        return new CryptoNormalizedRangeDto(stats.getSymbol(), stats.getNormalizedRange());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted(Comparator.comparingDouble(CryptoNormalizedRangeDto::getNormalizedRange).reversed())
                .collect(Collectors.toList());
    }

    public CryptoStats getHighestNormalizedRangeForDate(String date) throws Exception {
        // parse input date and convert it to start and end of the day
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        long startOfDay = localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        long endOfDay = localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;

        // fetch data using csvHandler
        List<CryptoData> data = csvHandler.parseCsvFiles();

        // filter data for the specific date
        List<CryptoData> dataForDate  = data.stream()
                .filter(d -> d.getTimestamp() >= startOfDay && d.getTimestamp() < endOfDay)
                .toList();

        if (dataForDate.isEmpty()) {
            throw new Exception("No data found for date: " + date);
        }

        return dataForDate.stream()
                .collect(Collectors.groupingBy(CryptoData::getSymbol))
                .entrySet().stream()
                .map(entry -> {
                    String symbol = entry.getKey();
                    List<CryptoData> cryptoData = entry.getValue();
                    double maxPrice = cryptoData.stream().mapToDouble(CryptoData::getPrice).max().getAsDouble();
                    double minPrice = cryptoData.stream().mapToDouble(CryptoData::getPrice).min().getAsDouble();
                    double normalizedRange = (maxPrice - minPrice) / minPrice;
                    return new CryptoStats(symbol, 0, 0, maxPrice, minPrice, normalizedRange);
                })
                .max(Comparator.comparingDouble(CryptoStats::getNormalizedRange))
                .orElseThrow(() -> new Exception("No crypto found with highest normalized range"));

    }

}
