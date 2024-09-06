package com.xm.cryptorec.cryptorec.interfaces;

import com.xm.cryptorec.cryptorec.dto.CryptoNormalizedRangeDto;
import com.xm.cryptorec.cryptorec.model.CryptoStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Crypto Recommendation API", description = "Research your options on our system's available crypto recommendations.")
public interface CryptoControllerInterface {

    @Operation(summary = "Retrieve all available cryptos by normalized range")
    List<CryptoNormalizedRangeDto> getCryptosSortedByNormalizedRange();

    @Operation(summary = "Retrieve available crypto stats by using crypto symbol as input")
    CryptoStats getCryptoStats(@PathVariable String symbol) throws Exception;

    @Operation(summary = "Retrieve the highest normalized range for a given date")
    @Parameter(name = "date", description = "The date for which you want to retrieve the highest normalized range", example = "2022-01-01")
    CryptoNormalizedRangeDto getCryptoWithHighestNormalizedRange(@PathVariable String date) throws Exception;
}
