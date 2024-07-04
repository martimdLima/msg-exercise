package com.martim.lima.exercicio.service;

import java.util.HashMap;
import java.util.Map;

public class MockExternalPopulationDataService implements ExternalPopulationDataInterface {

    @Override
    public Long getPopulationData(int year, String country, String gender) {
        // Mock data for population based on year, country, and gender
        return 1000000L; // Return a mock population number
    }

    @Override
    public Map<String, Long> getPopulationDataByCountry(int year, String country) {
        // Mock data for population based on year and country
        Map<String, Long> mockData = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            mockData.put("Gender" + i, 50000L + (i * 5000)); // Mock population data for multiple genders
        }
        return mockData;
    }

    @Override
    public Map<String, Long> getPopulationDataByGender(int year, String gender) {
        // Mock data for population based on year and gender
        Map<String, Long> mockData = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            mockData.put("Country" + i, 100000L + (i * 10000)); // Mock population data for multiple countries
        }
        return mockData;
    }

    @Override
    public Map<String, Map<String, Long>> getPopulationDataForYear(int year) {
        // Mock data for population based on year
        Map<String, Map<String, Long>> mockData = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            Map<String, Long> populationByGender = new HashMap<>();
            for (int j = 0; j < 2; j++) {
                populationByGender.put("Gender" + j, 50000L + (j * 5000) + (i * 10000)); // Mock male and female population
            }
            mockData.put("Country" + i, populationByGender);
        }

        return mockData;
    }
}
