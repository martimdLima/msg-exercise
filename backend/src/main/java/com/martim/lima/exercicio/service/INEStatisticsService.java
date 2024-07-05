package com.martim.lima.exercicio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.martim.lima.exercicio.exceptions.ExecutionInterruptionException;
import com.martim.lima.exercicio.exceptions.GeneralException;
import com.martim.lima.exercicio.exceptions.InterruptedThreadException;
import com.martim.lima.exercicio.exceptions.ParsingResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.martim.lima.exercicio.constants.MortalityRateConstants.ExceptionConstants.EXECUTION_INTERRUPTION_EXCEPTION_BASE_MSG;
import static com.martim.lima.exercicio.constants.MortalityRateConstants.ExceptionConstants.GENERAL_EXCEPTION_BASE_MSG;
import static com.martim.lima.exercicio.constants.MortalityRateConstants.ExceptionConstants.INTERRUPTED_THREAD_EXCEPTION_BASE_MSG;
import static com.martim.lima.exercicio.constants.MortalityRateConstants.ExceptionConstants.UNEXPECTED_EXCEPTION_FETCHING_POPULATION_MSG;

@Service
public class INEStatisticsService implements ExternalPopulationDataInterface {

    @Autowired
    private RestTemplate restTemplate;

    private static final String INE_API_URL = "https://www.ine.pt/ine/json_indicador/pindica.jsp?op=2";

    @Override
    public Map<String, Map<String, Long>> getPopulationDataForYear(int year) {
        Map<String, Map<String, Long>> result = new HashMap<>();
        Future<Map<String, Long>> populationFuture = this.getPopulationForYearAsync(year);

        try {
            result.put("PT", populationFuture.get());
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedThreadException(INTERRUPTED_THREAD_EXCEPTION_BASE_MSG);
        } catch (ExecutionException e) {
            throw new ExecutionInterruptionException(EXECUTION_INTERRUPTION_EXCEPTION_BASE_MSG);
        } catch (Exception e) {
            throw new GeneralException(UNEXPECTED_EXCEPTION_FETCHING_POPULATION_MSG);
        }
    }

    @Override
    public Map<String, Long> getPopulationDataByCountry(int year, String country) {
        Future<Map<String, Long>> populationFuture = this.getPopulationForYearAsync(year);

        try {
            return populationFuture.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedThreadException(INTERRUPTED_THREAD_EXCEPTION_BASE_MSG);
        } catch (ExecutionException e) {
            throw new ExecutionInterruptionException(EXECUTION_INTERRUPTION_EXCEPTION_BASE_MSG);
        } catch (Exception e) {
            throw new GeneralException(UNEXPECTED_EXCEPTION_FETCHING_POPULATION_MSG);
        }
    }

    @Override
    public Map<String, Long> getPopulationDataByGender(int year, String gender) {
        Map<String, Long> result = new HashMap<>();
        Future<Long> populationFuture = this.getPopulationForYearAndGenderAsync(year, gender);

        try {
            Long population = populationFuture.get();
            result.put("PT", population);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedThreadException(INTERRUPTED_THREAD_EXCEPTION_BASE_MSG);
        } catch (ExecutionException e) {
            throw new ExecutionInterruptionException(EXECUTION_INTERRUPTION_EXCEPTION_BASE_MSG);
        } catch (Exception e) {
            throw new GeneralException(UNEXPECTED_EXCEPTION_FETCHING_POPULATION_MSG);
        }
    }
    @Override
    public Long getPopulationData(int year, String country, String gender) {
        Future<Long> populationFuture = this.getPopulationForYearAndGenderAsync(year, gender);

        try {
            return populationFuture.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedThreadException(INTERRUPTED_THREAD_EXCEPTION_BASE_MSG);
        } catch (ExecutionException e) {
            throw new ExecutionInterruptionException(EXECUTION_INTERRUPTION_EXCEPTION_BASE_MSG);
        } catch (Exception e) {
            throw new GeneralException(UNEXPECTED_EXCEPTION_FETCHING_POPULATION_MSG);
        }
    }

    public Map<String, Long> getMaleAndFemalePopulation(int year) {
        try {
            return this.getPopulationForYearAsync(year).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedThreadException(INTERRUPTED_THREAD_EXCEPTION_BASE_MSG);
        } catch (ExecutionException e) {
            throw new ExecutionInterruptionException(EXECUTION_INTERRUPTION_EXCEPTION_BASE_MSG);
        } catch (Exception e) {
            throw new GeneralException(UNEXPECTED_EXCEPTION_FETCHING_POPULATION_MSG);
        }
    }

    private String genINEApiUrl(int year, String gender) {
        final String gender_query_parameter = switch (gender) {
            case "M", "Male" -> "1";
            case "F", "Female" -> "2";
            case "MF" -> "1,2";
            case "total" -> "T";
            default -> "1,2,T";
        };

        return String.format("%s&varcd=%s&Dim2=%s&Dim3=%s&Dim1=%s&lang=%s",
                    INE_API_URL, "0008235", "PT", gender_query_parameter, "S7A" + year, "EN");
    }

    @Async
    private CompletableFuture<Map<String, Long>> getPopulationForYearAsync(int year) {
        String url = genINEApiUrl(year, "MF");
        String response = restTemplate.getForObject(url, String.class);
        Map<String, Long> pops = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.path(0).path("Dados").path(""+year);
            for (JsonNode entry : dataNode) {
                if (entry.path("dim_3_t").asText().equals("M")) {
                    pops.put("M", entry.path("valor").asLong());
                } else if (entry.path("dim_3_t").asText().equals("F")) {
                    pops.put("F", entry.path("valor").asLong());
                } else {
                    pops.put("MF", entry.path("valor").asLong());
                }
            }
        } catch (IOException e) {
            throw new ParsingResponseException("An error occurred while trying to parse the api response");
        }
        return CompletableFuture.completedFuture(pops);
    }

    private CompletableFuture<Long> getPopulationForYearAndGenderAsync(int year, String gender) {
        String url = genINEApiUrl(year, gender);
        String response = restTemplate.getForObject(url, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.path(0).path("Dados").path(year);

            for (JsonNode entry : dataNode) {
                if (entry.path("dim_3_t").asText().equals(gender)) {
                    return CompletableFuture.completedFuture(entry.path("valor").asLong());
                }
            }
        } catch (IOException e) {
            throw new ParsingResponseException("An error occurred while trying to parse the api response");
        }
        return CompletableFuture.completedFuture(0L);
    }
}