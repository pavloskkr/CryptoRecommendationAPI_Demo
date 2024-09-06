package com.xm.cryptorec.cryptorec.controller;

import com.xm.cryptorec.cryptorec.dto.CryptoNormalizedRangeDto;
import com.xm.cryptorec.cryptorec.model.CryptoStats;
import com.xm.cryptorec.cryptorec.service.CryptoComputationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CryptoUIController {

    private final CryptoComputationService computationService;

    @Autowired
    public CryptoUIController(CryptoComputationService computationService) {
        this.computationService = computationService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<CryptoNormalizedRangeDto> cryptoList = computationService.getAllCryptosSortedByNormalizedRange();
        model.addAttribute("cryptos", cryptoList);
        return "index";
    }

    @GetMapping("/crypto/{symbol}")
    public String cryptoStats(@PathVariable String symbol, Model model) throws Exception {
        CryptoStats cryptoStats = computationService.computeStats(symbol);
        model.addAttribute("crypto", cryptoStats);
        return "crypto-stats";
    }

    @GetMapping("/crypto/highest-normalized")
    public String getCryptoWithHighestNormalizedRange(@RequestParam("date") String date, Model model) {
        try {
            // Get highest normalized range for the specific date
            CryptoStats stats = computationService.getHighestNormalizedRangeForDate(date);
            CryptoNormalizedRangeDto cryptoDto = new CryptoNormalizedRangeDto(stats.getSymbol(), stats.getNormalizedRange());

            // Re-add the crypto list so that the page still displays it
            List<CryptoNormalizedRangeDto> cryptoList = computationService.getAllCryptosSortedByNormalizedRange();
            model.addAttribute("cryptos", cryptoList);

            // Add the specific crypto result to be shown in the card
            model.addAttribute("crypto", cryptoDto);

        } catch (Exception e) {
            // Handle the exception by adding the error message to the model
            model.addAttribute("errorMessage", "Data not found for the selected date: " + date);

            // Re-add the crypto list so that the page still displays it
            List<CryptoNormalizedRangeDto> cryptoList = computationService.getAllCryptosSortedByNormalizedRange();
            model.addAttribute("cryptos", cryptoList);
        }

        return "index"; // Render the same index.html with the result or error
    }

}
