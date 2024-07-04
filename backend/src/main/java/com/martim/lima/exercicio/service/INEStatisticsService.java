package com.martim.lima.exercicio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class INEStatisticsService implements ExternalPopulationDataInterface {

    @Autowired
    private RestTemplate restTemplate;

    private static final String INE_API_URL = "https://www.ine.pt/ine/json_indicador/pindica.jsp?op=2";

    @Override
    public Map<String, Map<String, Long>> getPopulationDataForYear(int year) {
        Map<String, Long> subResult =  new HashMap<>();
        subResult.put("M", 0L);
        subResult.put("F", 1L);
        Map<String, Map<String, Long>> result = new HashMap<>();
        try {
            result.put("PT", this.getPopulationForYearAsync(year).get());
            return result;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Rethrow the exception to propagate it
            //throw e;

            result.put("PT", subResult);
            return result;
        }
    }

    @Override
    public Map<String, Long> getPopulationDataByCountry(int year, String country) {
        Map<String, Long> result =  new HashMap<>();
        result.put("M", 0L);
        result.put("F", 1L);
        try {
            return this.getPopulationForYearAsync(year).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Rethrow the exception to propagate it
            //throw e;
        }
        return result;
    }

    @Override
    public Map<String, Long> getPopulationDataByGender(int year, String gender) {

        Map<String, Long> result =  new HashMap<>();
        try {
            result.put("PT", this.getPopulationForYearAndGenderAsync(year, gender).get());
            return result;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Rethrow the exception to propagate it
            //throw e;
        }
        result.put("PT", 0L);
        return result;
    }
    @Override
    public Long getPopulationData(int year, String country, String gender) {
        Long result = 0L;
        try {
            return this.getPopulationForYearAndGenderAsync(year, gender).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Rethrow the exception to propagate it
            //throw e;
        }
        return result;
    }

    public Map<String, Long> getMaleAndFemalePopulation(int year) {
        Map<String, Long> result = new HashMap<>();
        result.put("M", 0L);
        result.put("F", 1L);
        try {
            return this.getPopulationForYearAsync(year).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

            // Rethrow the exception to propagate it
            //throw e;
            return result;
        }
    }

    private String buildUrl(String year, String gender) {
        String url = "";
        if (gender == null) {
            return "https://www.ine.pt/ine/json_indicador/pindica.jsp?op=2&varcd=0008235&Dim2=T&Dim1=S7A" + year + "&lang=EN";
        }

        if (gender.equals("M")) {
            url = "https://www.ine.pt/ine/json_indicador/pindica.jsp?op=2&varcd=0008235&Dim2=T&Dim3=1&Dim1=S7A" + year + "&lang=EN";
        }

        if (gender.equals("F")) {
            url = "https://www.ine.pt/ine/json_indicador/pindica.jsp?op=2&varcd=0008235&Dim2=T&Dim3=2&Dim1=S7A" + year + "&lang=EN";
        }

        if (gender.equals("MF")) {
            url = "https://www.ine.pt/ine/json_indicador/pindica.jsp?op=2&varcd=0008235&Dim2=T&Dim3=T&Dim1=S7A" + year + "&lang=EN";
        }
        return url;
    }

    public String genINEApiUrl(int year, String gender) {
        final String gender_query_paramter = switch (gender) {
            case "M", "Male" -> "1";
            case "F", "Female" -> "2";
            case "MF" -> "1,2";
            case "total" -> "T";
            default -> "1,2,T";
        };

        return INE_API_URL + "&varcd=0012903&Dim2=PT&Dim3=" + gender_query_paramter + "&Dim1=S7A" + year +  "&lang=EN&Dim4=T";
    }

    @Async
    private CompletableFuture<Map<String, Long>> getPopulationForYearAsync(int year) {
        String url = genINEApiUrl(year, "");
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(0L);
    }

    private Map<String, Long> getPopulationForYear(int year) {
        String url = genINEApiUrl(year, "");
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
            return pops;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pops;
    }

    private long getPopulationForYearAndGender(int year, String gender) {
        String url = genINEApiUrl(year, gender);
        String response = restTemplate.getForObject(url, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.path(0).path("Dados").path(year);

            for (JsonNode entry : dataNode) {
                if (entry.path("dim_3_t").asText().equals(gender)) {
                    return entry.path("valor").asLong();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long getDeathsForYearAndGender(String year, String gender) {
        String url = this.buildUrl(year, gender);
        String response = restTemplate.getForObject(url, String.class);
        Map<String, Long> deaths = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.path(0).path("Dados").path(year);

            for (JsonNode entry : dataNode) {
                if (entry.path("dim_3_t").asText().equals(gender)) {
                    return entry.path("valor").asLong();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Map<String, Long> getDeathsForYear(String year) {
        String url = this.buildUrl(year, null);
        String response = restTemplate.getForObject(url, String.class);
        Map<String, Long> deaths = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.path(0).path("Dados").path(year);

            for (JsonNode entry : dataNode) {
                if (entry.path("dim_3_t").asText().equals("M")) {
                    deaths.put("M", entry.path("valor").asLong());
                } else if (entry.path("dim_3_t").asText().equals("F")) {
                    deaths.put("F", entry.path("valor").asLong());
                } else {
                    deaths.put("MF", entry.path("valor").asLong());
                }
            }
            return deaths;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return deaths;
    }
}