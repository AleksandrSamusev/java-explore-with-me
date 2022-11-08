package ru.practicum.stat.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "hits")
@Data
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "app")
    String app;

    @Column(name = "uri")
    String uri;

    @Column(name = "ip")
    String ip;

    @Column(name = "date_time")
    String timestamp;

}
