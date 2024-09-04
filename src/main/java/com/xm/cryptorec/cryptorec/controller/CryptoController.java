package com.xm.cryptorec.cryptorec.controller;

import com.xm.cryptorec.cryptorec.dto.CryptoNormalizedRangeDto;
import com.xm.cryptorec.cryptorec.model.CryptoData;
import com.xm.cryptorec.cryptorec.model.CryptoStats;
import com.xm.cryptorec.cryptorec.service.CryptoComputationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cryptos")
public class CryptoController {

    private final CryptoComputationService computationService;

    @Autowired
    public CryptoController(CryptoComputationService computationService) {
        this.computationService = computationService;
    }

    @GetMapping("/normalized-range")
    public List<CryptoNormalizedRangeDto> getCryptosSortedByNormalizedRange() {
        return computationService.getAllCryptosSortedByNormalizedRange();
    }

    @GetMapping("/{symbol}/stats")
    public CryptoStats getCryptoStats(@PathVariable String symbol) throws Exception {
        return computationService.computeStats(symbol);
    }

    @GetMapping("/normalized-range/{date}")
    public CryptoNormalizedRangeDto getCryptoWithHighestNormalizedRange(@PathVariable String date) throws Exception {
        CryptoStats stats = computationService.getHighestNormalizedRangeForDate(date);
        return new CryptoNormalizedRangeDto(stats.getSymbol(), stats.getNormalizedRange());
    }
}
