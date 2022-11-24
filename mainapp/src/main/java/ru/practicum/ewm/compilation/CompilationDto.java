package ru.practicum.ewm.compilation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.EventShortDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompilationDto {
    @NotNull
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private Boolean pinned;
    private List<EventShortDto> events;
}
