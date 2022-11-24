package ru.practicum.ewm.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class Location {
    private Float lat;
    private Float lon;
}
