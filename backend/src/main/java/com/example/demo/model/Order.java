package com.example.demo.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Boolean active;

    @ManyToOne
    @ToString.Exclude
    private User createdBy;

    @JsonGetter("createdBy")
    public String getCreatedByEmail() {
        return createdBy != null ? createdBy.getEmail() : null;
    }


    @ManyToMany
    @JoinTable(
            name = "order_dish",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private List<Dish> dishes = new ArrayList<>();


    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime scheduledFor;

    @Transient
    private Integer cenaPorudzbine;

    @Version
    private Integer version = 0;

    @PostLoad
    private void onInit(){
        System.out.println("Postload: Order ID: " + this.id);
        System.out.println("Dishes: " + this.dishes);
        this.cenaPorudzbine = 0;
        for(Dish dish: this.dishes)
            cenaPorudzbine += dish.getCena();
    }

    @OneToOne
    @JsonIgnore
    @ToString.Exclude
    private ErrorMessage errorMessage;


    public enum OrderStatus {
        ORDERED,
        PREPARING,
        IN_DELIVERY,
        DELIVERED,
        CANCELLED
    }

    public enum PaymentType{
        CASH,
        CARD
    }

}