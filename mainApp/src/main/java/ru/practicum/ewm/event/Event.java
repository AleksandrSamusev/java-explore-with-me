package ru.practicum.ewm.event;

import lombok.Data;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    @ManyToOne
    @JoinColumn(name = "categoty_id", nullable = false)
    private Category category;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    @Column(name = "published_on", nullable = false)
    private LocalDateTime publishedOn;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventState state = EventState.PENDING;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "views", nullable = false)
    private Integer views;

}
