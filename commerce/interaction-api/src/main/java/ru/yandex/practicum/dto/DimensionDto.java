package ru.yandex.practicum.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    @NotBlank
    @Min(value = 1, message = "Ширина не должна быть меньше 1")
    private double width;
    @NotBlank
    @Min(value = 1, message = "Высота не должна быть меньше 1")
    private double height;
    @NotBlank
    @Min(value = 1, message = "Глубина не должна быть меньше 1")
    private double depth;
}
