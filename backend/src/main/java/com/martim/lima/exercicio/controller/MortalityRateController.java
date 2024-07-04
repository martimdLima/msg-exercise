package com.martim.lima.exercicio.controller;


import com.martim.lima.exercicio.exceptions.GeneralException;
import com.martim.lima.exercicio.exceptions.InvalidCSVException;
import com.martim.lima.exercicio.exceptions.ParsingCSVException;
import com.martim.lima.exercicio.models.MortalityRate;
import com.martim.lima.exercicio.service.INEStatisticsService;
import com.martim.lima.exercicio.service.MortalityRateService;
import com.martim.lima.exercicio.wrappers.AvailableYearsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.martim.lima.exercicio.utils.CountryCodeUtils.getCountryCode;
import static com.martim.lima.exercicio.utils.CountryCodeUtils.isValidCountryCode;

@CrossOrigin
@RestController
@RequestMapping("/api/mortality")
public class MortalityRateController {

    @Autowired
    private MortalityRateService mortalityRateService;

    @Autowired
    private INEStatisticsService ineStatisticsService;

    @GetMapping("/health")
    public ResponseEntity<HttpStatus> health() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/available/years")
    public ResponseEntity<AvailableYearsResponse> getAvailableYears() {
        List<Integer> years = mortalityRateService.getAllDistinctYears();
        AvailableYearsResponse response = new AvailableYearsResponse(years);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/id/{id}")
    public MortalityRate getById(@PathVariable long id) {
        return mortalityRateService.findById(id);
    }

    @GetMapping("/search/year/{year}")
    public List<MortalityRate> getByYear(@PathVariable int year) {
        return mortalityRateService.findByYear(year);
    }

    @GetMapping("/search/year/{year}/country/{country}")
    public MortalityRate getByYearAndCountry(@PathVariable int year,
                                         @PathVariable String country) {
        return mortalityRateService.findByYearAndCountry(year, country);
    }

    @PostMapping("/save")
    public ResponseEntity<MortalityRate> save(@RequestBody MortalityRate mortalityRate) {
        Map<String, Long> populationNumbers = ineStatisticsService.getMaleAndFemalePopulation(mortalityRate.getYear());
        mortalityRate.setMalePopulation(populationNumbers.get("M"));
        mortalityRate.setFemalePopulation(populationNumbers.get("F"));
        MortalityRate saveMortalityRateRecord = mortalityRateService.save(mortalityRate);
        return new ResponseEntity<>(saveMortalityRateRecord, HttpStatus.CREATED);
    }

    @PatchMapping("/update")
    public ResponseEntity<MortalityRate> update(@RequestBody MortalityRate mortalityRate) {
        MortalityRate updateMortalityRateRecord = mortalityRateService.update(mortalityRate);
        return new ResponseEntity<>(updateMortalityRateRecord, HttpStatus.OK);
    }
    @PostMapping("/upload")
    public void uploadCsv(@RequestParam("file") MultipartFile file, @RequestParam int year) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            List<MortalityRate> records = new ArrayList<>();
            int counter = 0;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");

                if (data.length != 3) {
                    throw new InvalidCSVException("Invalid data format: expected 3 columns but got " + data.length);
                }

                if (counter >= 1) {
                    String country = isValidCountryCode(data[0]) ? data[0] : getCountryCode(data[0]);
                    Map<String, Long> populationNumbers = ineStatisticsService.getMaleAndFemalePopulation(year);
                    MortalityRate mRateRecord = buildMortalityRateFromCsv(year, country, data, populationNumbers);
                    records.add(mRateRecord);
                }
                counter++;
            }
            mortalityRateService.deleteRecordsByYear(year);
            mortalityRateService.saveAllRecords(records);
        } catch (IOException e) {
            throw new ParsingCSVException("Error reading the file: " + e.getMessage());
        } catch (Exception e) {
            throw new GeneralException("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static MortalityRate buildMortalityRateFromCsv(Integer year, String country, String[] data, Map<String, Long> populationNumbers) {
        double maleRate;
        double femaleRate;

        try {
            maleRate = Double.parseDouble(data[1]);
            femaleRate = Double.parseDouble(data[2]);
        } catch (NumberFormatException e) {
            throw new InvalidCSVException("Invalid data format in CSV: " + e.getMessage());
        }

        return MortalityRate.builder()
                .year(year)
                .country(country)
                .femaleRate(femaleRate)
                .maleRate(maleRate)
                .malePopulation(populationNumbers.get("M"))
                .femalePopulation(populationNumbers.get("F"))
                .build();
    }
}