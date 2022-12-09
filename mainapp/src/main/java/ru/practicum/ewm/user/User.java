package ru.practicum.ewm.user;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    @Email
    private String email;

    @Column(name = "initiator_rating")
    private double initiatorRating;

    @Column(name = "reviewer_rating")
    private double reviewerRating;
}
