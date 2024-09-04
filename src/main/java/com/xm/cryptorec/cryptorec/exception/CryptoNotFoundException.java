package com.xm.cryptorec.cryptorec.exception;

import lombok.Getter;

@Getter
public class CryptoNotFoundException extends RuntimeException{

    private String symbol;

    public CryptoNotFoundException(String symbol, String message) {
        super(message);
        this.symbol = symbol;
    }
}
