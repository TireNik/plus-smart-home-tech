package ru.yandex.practicum.dto;

import lombok.Builder;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
public class BookedProductsDto {
    Double deliveryWeight;
    Double deliveryVolume;
    Boolean fragile;
}
