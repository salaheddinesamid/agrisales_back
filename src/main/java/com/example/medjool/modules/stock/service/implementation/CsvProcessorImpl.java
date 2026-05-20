package com.example.medjool.modules.stock.service.implementation;

import com.example.medjool.modules.stock.service.CsvProcessor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class CsvProcessorImpl implements CsvProcessor {
    @Override
    public CSVParser processCSV(MultipartFile file) {

        try(
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
                ) {
            return csvParser;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
