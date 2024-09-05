package com.xm.cryptorec.cryptorec.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CryptoStats implements Serializable {

    private String symbol;
    private double oldestPrice;
    private double newestPrice;
    private double maxPrice;
    private double minPrice;
    private double normalizedRange;

    // for deserialization
    public CryptoStats() {
    }

    public CryptoStats(String symbol, double oldestPrice, double newestPrice, double maxPrice, double minPrice, double normalizedRange) {
        this.symbol = symbol;
        this.oldestPrice = oldestPrice;
        this.newestPrice = newestPrice;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.normalizedRange = normalizedRange;
    }
}
