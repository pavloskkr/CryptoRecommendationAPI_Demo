package com.xm.cryptorec.cryptorec.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CryptoNormalizedRangeDto {

    private String crypto;
    private double normalizedRange;

    public CryptoNormalizedRangeDto(String crypto, double normalizedRange) {
        this.crypto = crypto;
        this.normalizedRange = normalizedRange;
    }
}
