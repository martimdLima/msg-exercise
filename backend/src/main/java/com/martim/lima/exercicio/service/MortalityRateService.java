package com.martim.lima.exercicio.service;

import com.martim.lima.exercicio.exceptions.ResourceNotFoundException;
import com.martim.lima.exercicio.models.MortalityRate;
import com.martim.lima.exercicio.repository.MortalityRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MortalityRateService implements GenericServiceInterface<MortalityRate, Long> {

    @Autowired
    private MortalityRateRepository repository;

    public List<Integer> getAllDistinctYears() {
        return repository.findDistinctYears();
    }

    @Override
    public MortalityRate findById(Long id) {
        Optional<MortalityRate> mortalityRateOpt = repository.findById(id);

        if (mortalityRateOpt.isEmpty()) {
            throw new ResourceNotFoundException("Mortality Rate not found with id: " + id);
        }

        return mortalityRateOpt.get();
    }

    public List<MortalityRate> findByYear(int year) {
        List<MortalityRate> mortalityRates = repository.findByYear(year);
        if (mortalityRates.isEmpty()) {
            throw new ResourceNotFoundException("No Mortality Rates found for year: " + year);
        }

        return repository.findByYear(year);
    }

    public MortalityRate findByYearAndCountry(int year, String country) {
        MortalityRate mortalityRate = repository.findByYearAndCountry(year, country);

        if (mortalityRate == null) {
            throw new ResourceNotFoundException("No Mortality Rate found for year: " + year + " and country: " + country);
        }

        return repository.findByYearAndCountry(year, country);
    }

    @Override
    public MortalityRate save(MortalityRate mRateRecords) {
        return repository.save(mRateRecords);
    }

    @Override
    public List<MortalityRate> saveAll(List<MortalityRate> records) {
        repository.saveAll(records);
        return records;
    }

    @Override
    public MortalityRate update(MortalityRate mRate) {
        MortalityRate existingMRate = this.findById(mRate.getId());
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
            throw new ResourceNotFoundException("No Mortality Rate found for id: " + id);
        }

        repository.delete(mortalityRateOpt.get());
    }

    @Override
    public void deleteAll(List<MortalityRate> records) {
        repository.deleteAll(records);
    }

    public void deleteRecordsByYear(int year) {
        List<MortalityRate> records = repository.findByYear(year);
        this.deleteAll(records);
    }

    public void saveAllRecords(List<MortalityRate> records) {
        this.saveAll(records);
    }
}
