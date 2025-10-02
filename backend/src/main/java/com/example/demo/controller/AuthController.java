package com.example.demo.controller;

import com.example.demo.request.LoginRequestt;
import com.example.demo.response.LoginResponsee;
import com.example.demo.service.UserServicee;
import com.example.demo.util.JwtUtill;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private final UserServicee userService;
    private final JwtUtill jwtUtill;
    private SimpMessagingTemplate messagingTemplate;


    public AuthController(AuthenticationManager authenticationManager, UserServicee userService, JwtUtill jwtUtill, SimpMessagingTemplate messagingTemplate) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtill = jwtUtill;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestt loginRequestt){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestt.getEmail(), loginRequestt.getPassword()));
        } catch (Exception   e){
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }


        return ResponseEntity.ok(new LoginResponsee(jwtUtill.generateToken(loginRequestt.getEmail())));
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("Invalid token format");
        }

        String jwt = token.substring(7);

        try {

            if (!jwtUtill.validateToken(jwt)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }


            String email = jwtUtill.extractUsername(jwt);

            if (!userService.existsByEmail(email)) {
                return ResponseEntity.status(404).body("User not found");
            }


            String newToken = jwtUtill.generateToken(email);

            return ResponseEntity.ok(new LoginResponsee(newToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to refresh token");
        }
    }


}
