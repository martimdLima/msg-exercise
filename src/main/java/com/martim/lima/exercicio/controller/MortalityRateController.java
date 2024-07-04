package com.martim.lima.exercicio.controller;


import com.martim.lima.exercicio.models.MortalityRate;
import com.martim.lima.exercicio.service.INEStatisticsService;
import com.martim.lima.exercicio.service.MortalityRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.martim.lima.exercicio.utils.CountryCodeUtils.getCountryCode;
import static com.martim.lima.exercicio.utils.CountryCodeUtils.isValidCountryCode;

@RestController
@RequestMapping("/api/mortality")
public class MortalityRateController {

    @Autowired
    private MortalityRateService mortalityRateService;

    @Autowired
    private INEStatisticsService ineStatisticsService;

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return new ResponseEntity<>(HttpStatus.OK);
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
                if (data.length == 3 && counter >= 1) {
                    String country = isValidCountryCode(data[0]) ? data[0] : getCountryCode(data[0]);
                    Map<String, Long> populationNumbers = ineStatisticsService.getMaleAndFemalePopulation(year);
                    MortalityRate mRateRecord = MortalityRate.builder()
                            .year(year)
                            .country(country)
                            .femaleRate(Double.parseDouble(data[2]))
                            .maleRate(Double.parseDouble(data[1]))
                            .malePopulation(populationNumbers.get("M"))
                            .femalePopulation(populationNumbers.get("F"))
                            .build();
                    records.add(mRateRecord);
                }
                counter++;
            }
            mortalityRateService.deleteRecordsByYear(year);
            mortalityRateService.saveAllRecords(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}