package ru.practicum.ewm.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    private Long id;

    @NotNull(message = "Ошибка! Широта не может быть пустой.")
    private Float lat;

    @NotNull(message = "Ошибка! Долгота не может быть пустой.")
    private Float lon;

    @NotNull(message = "Ошибка! Радиус не может быть пустой.")
    @Positive(message = "Ошибка! Радиус должен быть > 0.")
    private Float radius = 0f;

}