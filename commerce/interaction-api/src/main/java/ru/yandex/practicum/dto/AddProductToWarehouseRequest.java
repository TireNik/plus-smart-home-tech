package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AddProductToWarehouseRequest {
    String productId;
    @Min(value = 1)
    Integer quantity;
}
