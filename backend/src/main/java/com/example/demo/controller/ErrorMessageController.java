package com.example.demo.controller;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.service.ErrorMessageService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserServicee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/errors")
public class ErrorMessageController {

    private final ErrorMessageService errorMessageService;
    private final UserServicee userService;

    @Autowired
    public ErrorMessageController(ErrorMessageService errorMessageService, UserServicee userService) {
        this.errorMessageService = errorMessageService;
        this.userService = userService;
    }


    @GetMapping
    public Page<ErrorMessage> getErrors(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "3")Integer size) {
        boolean authorized = false;
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        // Provera da li korisnik ima permisiju "can_search_order"
        for (GrantedAuthority authority : grantedAuthorities) {
            if (authority.getAuthority().equals("can_search_order")) {
                authorized = true;
            }
        }


        if (authorized) {
            // Ako korisnik ima permisiju, vraćaju se sve porudžbine

            return this.errorMessageService.paginateForAllUsers(page, size);

        } else {
            // Ako korisnik nema permisiju, vraćaju se samo porudžbine koje je kreirao ulogovani korisnik
            User currentUser = getCurrentUser();

            return this.errorMessageService.paginateForLoggedUser(page, size, currentUser);
        }

    }



    private User getCurrentUser() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUserByEmail(currentEmail);
    }

}
