package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEventRequest {
    private String annotation;
    private Long categoryId;
    private String description;
    private LocalDateTime eventDate;
    private Long eventId;
    private Boolean paid;
    private Long participantLimit;
    private String title;
}
