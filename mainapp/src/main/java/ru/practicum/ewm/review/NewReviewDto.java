package ru.practicum.ewm.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewReviewDto {
    private Long id;
    @NotNull
    private Long eventId;
    @NotNull
    private Long reviewerId;
    @NotNull
    private Boolean review;
    @Length(max = 2000)
    private String comment;
}
