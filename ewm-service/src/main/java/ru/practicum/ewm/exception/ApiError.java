package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static util.Constants.PATTERN_FOR_DATETIME;

@Getter
@Setter
@ToString
public class ApiError {

    private HttpStatus status; // Код статуса HTTP-ответа

    private String reason; // Общее описание причины ошибки

    private String message; // Сообщение об ошибке

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_DATETIME)
    private LocalDateTime timestamp; // Дата и время когда произошла ошибка

    private List<String> errors; // Список стектрейсов или описания ошибок

}