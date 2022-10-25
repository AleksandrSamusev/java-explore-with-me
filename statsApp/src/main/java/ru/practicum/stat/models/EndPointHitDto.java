package ru.practicum.stat.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EndPointHitDto {

    private Long id;

    private String app;

    private String uri;

    private String ip;

    private LocalDateTime timestamp;
}
