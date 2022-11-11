package ru.practicum.ewm.location;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Location {
    private Double lat;

    private Double lon;
}
