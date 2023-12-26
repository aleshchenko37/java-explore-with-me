package ru.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private Long id; // Идентификатор категории

    @NotBlank(message = "Ошибка! Название категории не может быть пустым.")
    @Size(max = 50, message = "Ошибка! Название категории может содержать максимум 50 символов.")
    private String name; // Название категории

}