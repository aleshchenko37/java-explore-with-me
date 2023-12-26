package ru.practicum.ewm.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private Long id; // Идентификатор пользователя

    private String email; // Электронный адрес пользователя

    private String name; // Имя пользователя

}