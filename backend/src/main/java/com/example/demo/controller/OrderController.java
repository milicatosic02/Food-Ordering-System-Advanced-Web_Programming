package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.model.Order.OrderStatus;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserServicee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserServicee userService;
    private SimpMessagingTemplate messagingTemplate;


    @Autowired
    public OrderController(OrderService orderService, UserServicee userService, SimpMessagingTemplate messagingTemplate) {
        this.orderService = orderService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody Order order) {

        System.out.println("Zahtev za porudzbinu: " + order.getAddress() + " - " + order.getPaymentType() + " - " + "scheduled: " + order.getScheduledFor() + " - "+ order.getDishes());

        boolean authorized = false;
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for (GrantedAuthority authority : grantedAuthorities) {
            if (authority.getAuthority().equals("can_place_order")) {
                authorized = true;
            }
        }

        if (!authorized) {
            System.out.println("Korisnik: " + getCurrentUser().getFirstName() + " nema dozvolu da kreira porudzbinu");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to place an order!");
        }

        User loggedUser = getCurrentUser();
        order.setCreatedBy(loggedUser);
        System.out.println("Ulogovani korisnik: " + loggedUser.getEmail());
        order.setStatus(OrderStatus.ORDERED);
        order.setCreatedAt(LocalDateTime.now());
        order.setActive(true);

        int number;

        if (order.getScheduledFor() != null)
            order.setScheduledFor(order.getScheduledFor().plusHours(1)); // Dodajemo sat vremena unapred zbog vremenske zone


        Order savedOrder = orderService.save(order);
        System.out.println("Saved order ID: " + savedOrder.getId() + " - saved order jelo: " + savedOrder.getDishes());

        if(order.getScheduledFor() != null) {
            number = orderService.countScheduledOrdersAtTime(order.getScheduledFor()) - 1;
            System.out.println("Scheduled for number: " + number);
            List<Order> zakazane = orderService.getScheduledForTime(order.getScheduledFor());

            for(Order zakazanePorudzbine: zakazane)
                System.out.println("Zakazana porudzbina za zeljeno vreme: " + zakazanePorudzbine.getId());
        }
        else {
            number = orderService.countActiveOrders(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY);
            System.out.println("Preparing/In delivery  number: " + number);
        }

        // Provera broja aktivnih porudžbina
       // int activeOrdersCount = orderService.countActiveOrders(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY);

        try {
            if (number >= 3) {
                System.out.println("Broj porudzbina zakazanih i u pripremi: " + number);
                savedOrder.setStatus(OrderStatus.CANCELLED);
                savedOrder.setActive(false);
                savedOrder.setScheduledFor(null);

                ErrorMessage error = orderService.logError("Maksimalan broj istovremenih porudžbina dostignut!", savedOrder);

                savedOrder.setErrorMessage(error);
                orderService.save(savedOrder);


                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Maximum number of simultaneous orders reached!");
            }

            if(order.getScheduledFor() != null){
                orderService.scheduledOrder(savedOrder);
            }


        }catch (ObjectOptimisticLockingFailureException exception){
            System.out.println("Optimistic locking failure detected. Retrying...");
            return placeOrder(savedOrder);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

//    @PostMapping("/place")
//    public ResponseEntity<?> placeOrder2(@RequestBody Order order) {
//
//        System.out.println("Zahtev za porudzbinu: " + order.getAddress() + " - " + order.getPaymentType() + " - " + "scheduled: " + order.getScheduledFor() + " - " + order.getDishes());
//
//        boolean authorized = false;
//        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//
//        for (GrantedAuthority authority : grantedAuthorities) {
//            if (authority.getAuthority().equals("can_place_order")) {
//                authorized = true;
//            }
//        }
//
//        if (!authorized) {
//            System.out.println("Korisnik: " + getCurrentUser().getFirstName() + " nema dozvolu da kreira porudzbinu");
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to place an order!");
//        }
//
//        User loggedUser = getCurrentUser();
//        order.setCreatedBy(loggedUser);
//        System.out.println("Ulogovani korisnik: " + loggedUser.getEmail());
//        order.setStatus(OrderStatus.ORDERED);
//        order.setCreatedAt(LocalDateTime.now());
//        order.setActive(true);
//
//        if (order.getScheduledFor() != null) {
//            order.setScheduledFor(order.getScheduledFor().plusHours(1)); // Dodajemo sat vremena unapred zbog vremenske zone
//        }
//
//        try {
//            if (order.getScheduledFor() != null) {
//                // Provera broja zakazanih porudžbina u isto vreme
//                int scheduledOrdersCount = orderService.countScheduledOrdersAtTime(order.getScheduledFor());
//                if (scheduledOrdersCount >= 3) {
//                    System.out.println("Broj zakazanih porudzbina za " + order.getScheduledFor() + ": " + scheduledOrdersCount);
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Maximum number of scheduled orders for this time reached!");
//                }
//            } else {
//                // Provera broja aktivnih porudžbina sa statusima PREPARING i IN_DELIVERY
//                int activeOrdersCount = orderService.countActiveOrders(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY);
//                if (activeOrdersCount >= 3) {
//                    System.out.println("Broj aktivnih porudzbina u pripremi i isporuci: " + activeOrdersCount);
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Maximum number of active orders reached!");
//                }
//            }
//
//            Order savedOrder = orderService.save(order);
//            System.out.println("Saved order ID: " + savedOrder.getId() + " - saved order jelo: " + savedOrder.getDishes());
//
//            if (order.getScheduledFor() != null) {
//                orderService.scheduledOrder(savedOrder);
//            }
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
//
//        } catch (ObjectOptimisticLockingFailureException exception) {
//            System.out.println("Optimistic locking failure detected. Retrying...");
//            return placeOrder(order);
//        }
//    }


    @GetMapping("/history")
    public ResponseEntity<List<Order>> getHistory() {
        boolean authorized = false;
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        // Provera da li korisnik ima permisiju "can_search_order"
        for (GrantedAuthority authority : grantedAuthorities) {
            if (authority.getAuthority().equals("can_search_order")) {
                authorized = true;
            }
        }

        List<Order> orders;

        if (authorized) {
            // Ako korisnik ima permisiju, vraćaju se sve porudžbine
           // orders = orderService.findAll();
            orders = orderService.findOrderHistory();
            System.out.println("Korisnik ima permisiju");
            System.out.println("Orders: " + orders);
        } else {
            // Ako korisnik nema permisiju, vraćaju se samo porudžbine koje je kreirao ulogovani korisnik
            User currentUser = getCurrentUser();
            orders = orderService.findHistoryByUser(currentUser);
            System.out.println("Orders for user " + currentUser.getEmail() + ": " + orders);

        }

        return ResponseEntity.ok(orders);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOrderById(@PathVariable("id") Long id) {


        Optional<Order> optionalOrder = orderService.findById(id);

        if(optionalOrder.isPresent())
            return ResponseEntity.ok(optionalOrder.get());

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/track")
    public ResponseEntity<List<Order>> trackOrders() {
        boolean authorized = false;
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for (GrantedAuthority authority : grantedAuthorities) {
            if (authority.getAuthority().equals("can_track_order")) {
                authorized = true;
            }
        }

        List<Order> orders;

        if (authorized) {
            orders = orderService.findByActiveTrue();

        } else {
            User currentUser = getCurrentUser();
            orders = orderService.findByActiveTrueAndCreatedBy(currentUser);
        }
        return ResponseEntity.ok(orders);
    }



    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {

        boolean authorized = false;
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for (GrantedAuthority authority : grantedAuthorities) {
            if (authority.getAuthority().equals("can_cancel_order")) {
                authorized = true;
            }
        }

        if(authorized) {
            Optional<Order> order = orderService.findById(orderId);
            if (order.isPresent() && order.get().getStatus() == OrderStatus.ORDERED) {
                System.out.println("Status porudzbine koja zeli da se otkaze: " + order.get().getStatus());

                try {
                    order.get().setStatus(OrderStatus.CANCELLED);
                    order.get().setActive(false);
                    order.get().setScheduledFor(null);
                    orderService.save(order.get());

                    messagingTemplate.convertAndSend("/topic/order-status",new Message(order.get().getId(),OrderStatus.CANCELLED));

                }catch (ObjectOptimisticLockingFailureException exception){
                    System.out.println("Optimistic locking failure detected. Retrying...");
                    return this.cancelOrder(order.get().getId());
                }

            } else {
                return ResponseEntity.status(400).body("Order cannot be canceled");
            }
            return ResponseEntity.ok("Order canceled successfully");
        }
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to cancel an order!");

    }


    @GetMapping("/search")
    public ResponseEntity<List<Order>> searchOrders(
            @RequestParam(required = false) String user, // Korisnik
            @RequestParam(required = false) Order.OrderStatus status, // Status
            @RequestParam(required = false) String dateFrom, // Datum
            @RequestParam(required = false) String dateTo // Datum
    ) {



        // Poziv servisa za pretragu sa prosleđenim parametrima
        List<Order> orders = orderService.searchOrders(user, status, dateFrom, dateTo);

        // Vraćanje filtriranih rezultata
        return ResponseEntity.ok(orders);
    }



    private User getCurrentUser() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUserByEmail(currentEmail);
    }
}
