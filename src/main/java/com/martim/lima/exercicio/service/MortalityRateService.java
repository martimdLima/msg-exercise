package com.martim.lima.exercicio.service;

import com.martim.lima.exercicio.models.MortalityRate;
import com.martim.lima.exercicio.repository.MortalityRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MortalityRateService implements IService<MortalityRate, Long> {

    @Autowired
    private MortalityRateRepository repository;

    @Override
    public MortalityRate findById(Long id) {
        Optional<MortalityRate> mortalityRateOpt = repository.findById(id);

        if (mortalityRateOpt.isEmpty()) {
            throw new RuntimeException("Not Found");
        }

        return mortalityRateOpt.get();
    }

    public List<MortalityRate> findByYear(int year) {
        return repository.findByYear(year);
    }

    public MortalityRate findByYearAndCountry(int year, String country) {
        return repository.findByYearAndCountry(year, country);
    }
    @Override
    public MortalityRate save(MortalityRate record) {
        // Assume we have an external service or mock for population data
        //record.setMalePopulation(getPopulationData(record.getCountry(), "male"));
        //record.setFemalePopulation(getPopulationData(record.getCountry(), "female"));
        return repository.save(record);
    }
    @Override
    public MortalityRate update(MortalityRate mRate) {
        MortalityRate existingMRate = repository.findById(mRate.getId()).orElseThrow(() -> new RuntimeException("Not Found"));
        existingMRate.setYear(mRate.getYear());
        existingMRate.setCountry(mRate.getCountry());
        existingMRate.setMaleRate(mRate.getMaleRate());
        existingMRate.setFemaleRate(mRate.getFemaleRate());
        repository.save(existingMRate);
        return existingMRate;
    }
    @Override
    public void delete(Long id) {
        Optional<MortalityRate> mortalityRateOpt = repository.findById(id);

        if (mortalityRateOpt.isEmpty()) {
            throw new RuntimeException("Not Found");
        }

        repository.delete(mortalityRateOpt.get());
    }

    public void deleteRecordsByYear(int year) {
        List<MortalityRate> records = repository.findByYear(year);
        repository.deleteAll(records);
    }

    public void saveAllRecords(List<MortalityRate> records) {
        /*records.forEach(record -> {
            record.setMalePopulation(getPopulationData(record.getCountry(), "male"));
            record.setFemalePopulation(getPopulationData(record.getCountry(), "female"));
        });*/
        repository.saveAll(records);
    }

    private long getPopulationData(String country, String gender) {
        // Mock implementation, should call external service
        return 5000000; // Replace with actual logic to fetch population data
    }
}
