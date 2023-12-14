package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static util.Constants.PATTERN_FOR_DATETIME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHit {

    private Long id;

    @NotBlank(message = "Ошибка! Идентификатор сервиса не может быть пустым.")
    private String app;

    @NotBlank(message = "Ошибка! URI, для которого был осуществлен запрос, не может быть пустым.")
    private String uri;

    @NotBlank(message = "Ошибка! IP-адрес пользователя, осуществившего запрос, не может быть пустым.")
    private String ip;

    @NotBlank(message = "Ошибка! Дата и время, когда был совершен запрос к эндпоинту, не могут быть пустыми.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_DATETIME)
    private LocalDateTime timestamp;

}