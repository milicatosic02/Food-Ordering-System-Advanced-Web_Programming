package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserServicee;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserServicee userService;
    private PasswordEncoder passwordEncoder;


    public UserController(UserServicee userService,PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "/all",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers() {
        boolean authorized = false;
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for(GrantedAuthority authority : grantedAuthorities) {

            if(authority.getAuthority().equals("can_read_users") || authority.getAuthority().equals("can_create_users") || authority.getAuthority().equals("can_update_users") || authority.getAuthority().equals("can_delete_users"))
                authorized = true;
        }

        if(!authorized)
            return ResponseEntity.status(403).body("You don't have permission to read users!");

        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);

    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserById(@PathVariable("userId") Long userId) {
        boolean authorized = false;
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for(GrantedAuthority authority : grantedAuthorities) {

            if(authority.getAuthority().equals("can_read_users") || authority.getAuthority().equals("can_create_users") || authority.getAuthority().equals("can_update_users") || authority.getAuthority().equals("can_delete_users"))
                authorized = true;

        }

        if(!authorized)
            return ResponseEntity.status(403).body("You don't have permission to read users!");

        Optional<User> optionalUser = userService.findById(userId);

        if(optionalUser.isPresent())
            return ResponseEntity.ok(optionalUser.get());

        return ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody User user) {
        boolean authorized = false;
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for(GrantedAuthority authority : grantedAuthorities) {

            if(authority.getAuthority().equals("can_create_users"))
                authorized = true;

        }

        if(!authorized)
            return ResponseEntity.status(403).body("You don't have permission to create a user!");

        if(userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(409).body("Email already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return ResponseEntity.ok().body(userService.save(user));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody User user, @RequestParam("userId") Long userId) {
        boolean authorized = false;
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for(GrantedAuthority authority : grantedAuthorities) {

            if(authority.getAuthority().equals("can_update_users"))
                authorized = true;
        }

        if(!authorized)
            return ResponseEntity.status(403).body("You don't have permission to update users!");

        String oldUserEmail = userService.findById(userId).get().getEmail();

        if(!oldUserEmail.equals(user.getEmail()) && userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(409).body("Email already exists!");
        }

        Optional<User> optionalUser = userService.updateUser(user,userId);

        if(!optionalUser.isPresent())
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok().body(optionalUser.get());
    }


    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long id){
        boolean authorized = false;
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for(GrantedAuthority authority : grantedAuthorities) {

            if(authority.getAuthority().equals("can_delete_users"))
                authorized = true;

        }

        if(!authorized)
            return ResponseEntity.status(403).body("You don't have permission to delete users!");

        userService.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
