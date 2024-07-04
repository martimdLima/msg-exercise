package com.martim.lima.exercicio.wrappers;

import java.util.List;

public class AvailableYearsResponse {
    private List<Integer> years;

    public AvailableYearsResponse(List<Integer> years) {
        this.years = years;
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }
}