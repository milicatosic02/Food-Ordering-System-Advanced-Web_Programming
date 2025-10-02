package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ErrorMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private LocalDateTime timestamp;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
