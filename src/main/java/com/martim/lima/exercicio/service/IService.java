package com.martim.lima.exercicio.service;

import java.util.Optional;

public interface IService <T, ID> {

    T findById(ID id);

    T save(T entity);

    T update(T entity);

    void delete(ID id);
}