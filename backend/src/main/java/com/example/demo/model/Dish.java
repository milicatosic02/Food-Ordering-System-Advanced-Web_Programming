package com.example.demo.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dish")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dish_name", length = 64, nullable = false)
    private String dishName;

    @Column(name = "description", length = 256)
    private String description;

    @Column(name ="cena", nullable = false)
    private Integer cena;

    @Column(name = "is_breakfast", nullable = false)
    private boolean breakfast = false;

    @Column(name = "is_lunch", nullable = false)
    private boolean lunch = false;

    @Column(name = "is_dinner", nullable = false)
    private boolean dinner = false;

    @Column(name = "is_desserts", nullable = false)
    private boolean desserts = false;

    @Column(name = "image_url", length = 512)
    private String imageUrl;


    @ManyToMany(mappedBy = "dishes")
    @JsonIgnore
    @ToString.Exclude
    private List<Order> orders = new ArrayList<>();



}

