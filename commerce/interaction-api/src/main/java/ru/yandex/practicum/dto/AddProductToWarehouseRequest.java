package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AddProductToWarehouseRequest {
    String productId;
    @Min(value = 1)
    Integer quantity;
}
