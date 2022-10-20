package ru.practicum.ewm.event;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventShortDto {

    private String annotation;
    private CategoryDto categoryDto;
    private Long confirmedRequests;
    private LocalDateTime eventDate;
    private Long eventId;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private Integer views;

}
