package com.example.demo.service;


import java.util.List;
import java.util.Optional;

public interface IServicee<T, ID> {
    <S extends T> S save(S var1);

    Optional<T> findById(ID var1);

    List<T> findAll();

    void deleteById(ID var1);

}
