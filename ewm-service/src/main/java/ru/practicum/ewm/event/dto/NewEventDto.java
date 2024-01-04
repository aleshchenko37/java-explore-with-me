package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.location.dto.LocationDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static util.Constants.PATTERN_FOR_DATETIME;

@Data
public class NewEventDto {

    @NotBlank(message = "Ошибка! Краткое описание события не может быть пустым.")
    @Size(min = 20, max = 2000, message =
            "Ошибка! Краткое описание события может содержать минимум 20, максимум 2000 символов.")
    private String annotation; // Краткое описание события

    @NotNull(message = "Ошибка! id категории, к которой относится событие, не может быть пустым.")
    private Long category; // id категории, к которой относится событие

    @NotBlank(message = "Ошибка! Полное описание события не может быть пустым.")
    @Size(min = 20, max = 7000, message =
            "Ошибка! Полное описание события может содержать минимум 20, максимум 7000 символов.")
    private String description; // Полное описание события

    @NotNull(message = "Ошибка! Дата и время, на которые намечено событие, не могут быть пустыми.")
    @FutureOrPresent(message = "Ошибка! Дата события должна еще не наступить.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_DATETIME)
    private LocalDateTime eventDate;
    // Дата и время, на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"

    @NotNull(message = "Ошибка! Широта и долгота места проведения события не могут быть пустыми.")
    private LocationDto location; // Широта и долгота места проведения события

    private Boolean paid = false; // Нужно ли оплачивать участие в событии

    private Integer participantLimit = 0;
    // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    private Boolean requestModeration = true;
    // Нужна ли пре-модерация заявок на участие. Если true, то все заявки будут ожидать подтверждения
    // инициатором события. Если false - то будут подтверждаться автоматически.

    @NotBlank(message = "Ошибка! Заголовок события не может быть пустым.")
    @Size(min = 3, max = 120, message =
            "Ошибка! Заголовок события может содержать минимум 3, максимум 120 символов.")
    private String title; // Заголовок события

}