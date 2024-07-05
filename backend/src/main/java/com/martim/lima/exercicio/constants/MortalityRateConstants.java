package com.martim.lima.exercicio.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MortalityRateConstants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ExceptionConstants {
        public static final String INTERRUPTED_THREAD_EXCEPTION_BASE_MSG = "Thread was interrupted while fetching population data.";
        public static final String EXECUTION_INTERRUPTION_EXCEPTION_BASE_MSG = "Execution exception occurred while fetching population data«";
        public static final String GENERAL_EXCEPTION_BASE_MSG = "Unexpected exception occurred ";
        public static final String UNEXPECTED_EXCEPTION_FETCHING_POPULATION_MSG = GENERAL_EXCEPTION_BASE_MSG + " while fetching population data";
    }
}
