package ru.practicum.stat.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto {
    private int id;

    private String app;

    private String uri;

    private Integer hits;
}
