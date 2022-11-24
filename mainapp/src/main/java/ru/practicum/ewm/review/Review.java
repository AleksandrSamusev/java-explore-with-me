package ru.practicum.ewm.review;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "reviews")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User reviewer;

    @NotNull
    @Column(name = "review")
    private Boolean review;

    @Length(max = 2000)
    @Column(name = "comment")
    private String comment;


}
