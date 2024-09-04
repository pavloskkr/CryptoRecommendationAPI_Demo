package com.xm.cryptorec.cryptorec.service;

import com.xm.cryptorec.cryptorec.model.CryptoData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvHandler {

    private static final String CSV_DIR = "src/main/resources/data/";

    public List<CryptoData> parseCsvFiles() throws IOException {
        List<CryptoData> allCryptoData = new ArrayList<>();

        //Dynamic scan of the /data folder
        File folder = new File(CSV_DIR);
        File[] csvFiles = folder.listFiles(((dir, name) -> name.toLowerCase().endsWith(".csv")));

        if (csvFiles == null || csvFiles.length == 0) {
            throw new IOException("There are no .csv files in directory : " + CSV_DIR);
        }

        // .csv iteration
        for (File csvFile : csvFiles) {
            allCryptoData.addAll(parseCsvFile(csvFile));
        }

        return allCryptoData;
    }

    private List<CryptoData> parseCsvFile(File csvFile) {
        List<CryptoData> cryptoDataList = new ArrayList<>();

        // .csv parsing with commons-csv
        try (Reader in = new FileReader(csvFile)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setDelimiter(',')
                    .setHeader("timestamp", "symbol", "price")
                    .setSkipHeaderRecord(true)
                    .build().parse(in);

            // record iteration
            for (CSVRecord record : records) {
                long timestamp = Long.parseLong(record.get(0));
                String symbol = record.get(1);
                double price = Double.parseDouble(record.get(2));

                cryptoDataList.add(new CryptoData(timestamp, symbol, price));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cryptoDataList;
    }
}
