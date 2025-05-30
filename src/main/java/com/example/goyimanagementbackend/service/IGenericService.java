package com.example.goyimanagementbackend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IGenericService<T> {
    List<T> findAll();
    Page<T> findAll(Pageable pageable);
    Optional<T> findById(Long id);
    T create(T entity);
    T update(Long id, T entity);
    void softDelete(Long id);
    List<T> findByStatus(String status);
    Page<T> findByStatus(String status, Pageable pageable);
}