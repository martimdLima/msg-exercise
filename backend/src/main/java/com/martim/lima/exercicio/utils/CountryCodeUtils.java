package com.martim.lima.exercicio.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountryCodeUtils {

    // A map to store country names and their corresponding country codes
    private static final Map<String, String> COUNTRY_CODES = new HashMap<>();

    static {
        // Populate the map with country names and their codes
        for (String iso : Locale.getISOCountries()) {
            Locale locale = new Locale("", iso);
            COUNTRY_CODES.put(locale.getDisplayCountry().toLowerCase(), iso);
        }
    }

    public static String getCountryCode(String countryName) {
        if (countryName == null || countryName.isEmpty()) {
            return null;
        }

        // Convert the country name to lowercase for case-insensitive lookup
        String code = COUNTRY_CODES.get(countryName.toLowerCase());

        if (code == null) {
            return "Unknown country";
        }

        return code;
    }

    public static boolean isValidCountryCode(String code) {
        if (code == null || code.length() != 2) {
            return false;
        }

        // Convert the code to uppercase to ensure case-insensitive comparison
        return COUNTRY_CODES.containsValue(code.toUpperCase());
    }
}
