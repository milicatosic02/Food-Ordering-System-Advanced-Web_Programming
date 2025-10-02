package com.example.demo.response;

import lombok.Data;

@Data
public class LoginResponsee {
    private String jwt;

    public LoginResponsee(String jwt) {
        this.jwt = jwt;
    }
}
