package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.request.model.StateRequest;

import java.time.LocalDateTime;

import static util.Constants.PATTERN_FOR_DATETIME;

@Data
@Builder
public class ParticipationRequestDto {

    private Long id; // Идентификатор заявки

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_DATETIME)
    private LocalDateTime created; // Дата и время создания заявки

    private Long event; // Идентификатор события

    private Long requester; // Идентификатор пользователя, отправившего заявку

    private StateRequest status; // Статус заявки

}