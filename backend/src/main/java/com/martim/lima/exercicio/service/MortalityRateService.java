package com.martim.lima.exercicio.service;

import com.martim.lima.exercicio.exceptions.ResourceAlreadyExistsException;
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
        return mortalityRateOpt.orElse(null);

    }

    public List<MortalityRate> findByYear(int year) {
        List<MortalityRate> mortalityRates = repository.findByYear(year);
        if (mortalityRates.isEmpty()) {
            throw new ResourceNotFoundException("No Mortality Rates found for year: " + year);
        }

        return mortalityRates;
    }
    public List<MortalityRate> findByCountry(String country) {
        List<MortalityRate> mortalityRates = repository.findByCountry(country);
        if (mortalityRates.isEmpty()) {
            throw new ResourceNotFoundException("No Mortality Rates found for country: " + country);
        }

        return mortalityRates;
    }

    public MortalityRate findByYearAndCountry(int year, String country) {
        MortalityRate mortalityRate = repository.findByYearAndCountry(year, country);

        if (mortalityRate == null) {
            throw new ResourceNotFoundException("No Mortality Rate found for year: " + year + " and country: " + country);
        }

        return mortalityRate;
    }

    @Override
    public MortalityRate save(MortalityRate mRateRecord) {

        MortalityRate mortalityRate = repository.findByYearAndCountry(mRateRecord.getYear(), mRateRecord.getCountry());

        if (mortalityRate != null) {
            throw new ResourceAlreadyExistsException("Mortality Rate for: " + mRateRecord.getYear() + " and " +
                    "country: " + mRateRecord.getCountry() + " already exits");
        }

        return repository.save(mRateRecord);
    }

    @Override
    public List<MortalityRate> saveAll(List<MortalityRate> records) {
        records.stream().filter(mortalityRate -> repository.findByYearAndCountry(mortalityRate.getYear(), mortalityRate.getCountry()) != null).forEach(mortalityRate -> {
            throw new ResourceAlreadyExistsException("Mortality Rate for: " + mortalityRate.getYear() + " and " +
                    "country: " + mortalityRate.getCountry() + " already exits");
        });
        repository.saveAll(records);
        return records;
    }

    @Override
    public MortalityRate update(MortalityRate mRate) {
        MortalityRate existingMRate = this.findById(mRate.getId());

        if (existingMRate == null) {
            throw new ResourceNotFoundException("Mortality Rate with id: " + mRate.getId() + " was not found");
        }

        existingMRate.setYear(mRate.getYear());
        existingMRate.setCountry(mRate.getCountry());
        existingMRate.setMaleRate(mRate.getMaleRate());
        existingMRate.setFemaleRate(mRate.getFemaleRate());
        repository.save(existingMRate);
        return existingMRate;
    }
    @Override
    public void delete(Long id) {
        MortalityRate existingMRate = this.findById(id);

        if (existingMRate == null) {
            throw new ResourceNotFoundException("Mortality Rate with id: " + id + " was not found");
        }

        repository.delete(existingMRate);
    }

    @Override
    public void deleteAll(List<MortalityRate> records) {

        records.forEach(mortalityRate -> {
            MortalityRate existingMRate = this.findById(mortalityRate.getId());
            if (existingMRate == null) {
                throw new ResourceNotFoundException("Mortality Rate with id: " + mortalityRate.getId() + " was not found");
            }
        });

        repository.deleteAll(records);
    }

    public void deleteRecordsByYear(int year) {
        List<MortalityRate> records = repository.findByYear(year);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException("There aren't any Mortality Rate entries for the provided year:" + year);
        }

        this.deleteAll(records);
    }
}
