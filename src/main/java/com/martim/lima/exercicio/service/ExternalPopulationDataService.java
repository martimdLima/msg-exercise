package com.martim.lima.exercicio.service;

import java.util.Map;


public interface ExternalPopulationDataService {

    /**
     * Retrieves population data for a given year, country, and gender.
     *
     * @param year    the year for which the population data is requested
     * @param country the country for which the population data is requested
     * @param gender  an optional gender parameter; if empty, data for all genders is retrieved
     * @return the population data for the given year, country, and gender
     */
    Long getPopulationData(int year, String country, String gender);

    /**
     * Retrieves population data for a given year and country.
     *
     * @param year    the year for which the population data is requested
     * @param country the country for which the population data is requested
     * @return a map of gender to population data for the given year and country
     */
    Map<String, Long> getPopulationDataByCountry(int year, String country);

    /**
     * Retrieves population data for a given year and gender.
     *
     * @param year   the year for which the population data is requested
     * @param gender the gender for which the population data is requested
     * @return a map of country to population data for the given year and gender
     */
    Map<String, Long> getPopulationDataByGender(int year, String gender);

    /**
     * Retrieves population data for a given year.
     *
     * @param year the year for which the population data is requested
     * @return a map of country and gender to population data for the given year
     */
    Map<String, Map<String, Long>> getPopulationDataForYear(int year);
}