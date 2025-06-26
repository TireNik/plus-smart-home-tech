package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddProductToWarehouseRequest {
    @NotBlank
    UUID productId;

    @NotBlank
    @Min(value = 1, message = "Количество не может быть меньше 1")
    Long quantity;
}