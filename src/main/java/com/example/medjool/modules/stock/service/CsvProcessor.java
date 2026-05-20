package com.example.medjool.modules.stock.service;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CsvProcessor {

    /**
     * Process CSV file to extract necessary information.
     * It takes a multipart file as an input.
     * Returns list of CSV records.
     * @param file
     * @return list of records.
     */
    CSVParser processCSV(MultipartFile file);
}
