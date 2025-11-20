package com.mertcanengin.api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IService<T> {

    T save(T t);

    T getById(Integer id);

    List<T> getAll();

    Page<T> getAll(Pageable pageable);

    void delete(Integer id);
}
