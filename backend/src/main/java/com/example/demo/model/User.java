package com.example.demo.model;


import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 32,nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 32,nullable = false)
    private String lastName;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    //@ToString.Exclude
    @JsonIgnore
    private List<Order> orders;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "can_create_users", column = @Column(name = "can_create_users")),
            @AttributeOverride(name = "can_read_users", column = @Column(name = "can_read_users")),
            @AttributeOverride(name = "can_update_users", column = @Column(name = "can_update_users")),
            @AttributeOverride(name = "can_delete_users", column = @Column(name = "can_delete_users"))
    })
    private Roles roles = new Roles();


}
