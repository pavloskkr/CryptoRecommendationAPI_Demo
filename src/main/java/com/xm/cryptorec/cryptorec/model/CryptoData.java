package com.xm.cryptorec.cryptorec.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CryptoData {

    private long timestamp;
    private String symbol;
    private double price;

    public CryptoData(long timestamp, String symbol, double price) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.price = price;
    }

}
