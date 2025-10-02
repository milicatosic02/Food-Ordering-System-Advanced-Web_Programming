package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.DishRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DishService implements IServicee<Dish, Long>{

    private final DishRepository dishRepository;

    @Autowired
    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Override
    public <S extends Dish> S save(S var1) {
        return null;
    }

    @Override
    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id);
    }

    @Override
    public List<Dish> findAll() {
        return (List<Dish>)dishRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        dishRepository.deleteById(id);
    }

}
