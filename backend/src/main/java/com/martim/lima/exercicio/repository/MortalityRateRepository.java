package com.martim.lima.exercicio.repository;

import com.martim.lima.exercicio.models.MortalityRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MortalityRateRepository extends JpaRepository<MortalityRate, Long> {

    List<MortalityRate> findByYear(int year);
    MortalityRate findByYearAndCountry(int year, String country);
    @Query("SELECT DISTINCT m.year FROM MortalityRate m ORDER BY m.year ASC")
    List<Integer> findDistinctYears();
}