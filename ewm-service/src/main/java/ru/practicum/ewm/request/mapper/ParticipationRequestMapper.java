package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.ParticipationRequest;

import java.util.List;
import java.util.stream.Collectors;


public class ParticipationRequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .requester(participationRequest.getRequester().getId())
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent().getId())
                .status(participationRequest.getStatus())
                .build();
    }

    public static  List<ParticipationRequestDto> convertParticipationRequestToDtoList(List<ParticipationRequest> requests) {
        return requests.stream().map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }
}