package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static util.Constants.PATTERN_FOR_DATETIME;

@Data
public class EndpointHit {

    private Long id; // Идентификатор записи

    @NotBlank(message = "Ошибка! Идентификатор сервиса не может быть пустым.")
    private String app; // Идентификатор сервиса, для которого записывается информация

    @NotBlank(message = "Ошибка! URI, для которого был осуществлен запрос, не может быть пустым.")
    private String uri; // URI, для которого был осуществлен запрос

    @NotBlank(message = "Ошибка! IP-адрес пользователя, осуществившего запрос, не может быть пустым.")
    private String ip; // IP-адрес пользователя, осуществившего запрос

    @NotBlank(message = "Ошибка! Дата и время, когда был совершен запрос к эндпоинту, не могут быть пустыми.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_DATETIME)
    private LocalDateTime timestamp; //  Дата и время, когда был совершен запрос к эндпоинту

}