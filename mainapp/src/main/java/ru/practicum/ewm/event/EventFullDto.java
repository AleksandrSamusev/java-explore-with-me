package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.user.UserShortDto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String annotation;
    @NotNull
    private CategoryDto category;
    @NotNull
    private Boolean paid;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private UserShortDto initiator;
    private String description;
    private Integer participantLimit;
    @Enumerated(EnumType.STRING)
    private EventState state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @NotNull
    private Location location;
    private Boolean requestModeration = true;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private long confirmedRequests;
    private Integer views;
    private double rating;
}
