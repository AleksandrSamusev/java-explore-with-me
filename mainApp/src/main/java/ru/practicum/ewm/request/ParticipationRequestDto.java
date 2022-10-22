package ru.practicum.ewm.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipationRequestDto {
    private Long requestId;
    @NotNull
    private Long eventId;
    private LocalDateTime created = LocalDateTime.now();
    @NotNull
    private Long requesterId;
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;


}
