package com.example.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class Message {

    private Long orderId;
    private Order.OrderStatus status;



    public Message(Long orderId, Order.OrderStatus status) {
        this.orderId = orderId;
        this.status = status;
    }
}
