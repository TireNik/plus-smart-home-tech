package ru.yandex.practicum.dto;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.type.QuantityState;

@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SetProductQuantityStateRequest {
    String productId;
    QuantityState quantityState;
}
