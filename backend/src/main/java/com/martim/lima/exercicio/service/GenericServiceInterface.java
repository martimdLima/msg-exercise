package com.martim.lima.exercicio.service;

import java.util.List;

public interface GenericServiceInterface<T, ID> {

    T findById(ID id);

    //List<T> findAll(); // Added findAll method for completeness

    List<T> saveAll(List<T> entities);

    T save(T entity);

    T update(T entity);

    void delete(ID id);

    void deleteAll(List<T> records);
}