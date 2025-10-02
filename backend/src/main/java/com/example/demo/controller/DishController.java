package com.example.demo.controller;

import com.example.demo.model.Dish;
import com.example.demo.model.User;
import com.example.demo.service.DishService;
import com.example.demo.service.UserServicee;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;


    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllDishes() {
        boolean authorized = false;
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        List<Dish> dishes = dishService.findAll();
        return ResponseEntity.ok(dishes);

    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDishById(@PathVariable("id") Long id) {


        Optional<Dish> optionalDish = dishService.findById(id);

        if(optionalDish.isPresent())
            return ResponseEntity.ok(optionalDish.get());

        return ResponseEntity.notFound().build();
    }
}
