package ru.practicum.stat.models;

import lombok.Data;

@Data
public class ViewStats {

    private int id;

    private String app;

    private String uri;

    private Integer hits;

}
