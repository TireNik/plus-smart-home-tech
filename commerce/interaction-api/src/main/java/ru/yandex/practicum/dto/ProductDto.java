package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.type.ProductCategory;
import ru.yandex.practicum.type.ProductState;
import ru.yandex.practicum.type.QuantityState;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {
    String productId;
    String productName;
    String description;
    String imageSrc;
    QuantityState quantityState;
    ProductState productState;
    ProductCategory productCategory;
    @Min(value = 1)
    Float price;
}
