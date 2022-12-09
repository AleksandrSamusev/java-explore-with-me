package ru.practicum.ewm.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.EventShortDto;
import ru.practicum.ewm.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullReviewDto {
    private Long id;
    private EventShortDto event;
    private UserShortDto reviewer;
    private Boolean review;
    private String comment;
    private LocalDateTime created;

}
