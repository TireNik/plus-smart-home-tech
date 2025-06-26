package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.type.QuantityState;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SetProductQuantityStateRequest {
    @NotNull
    UUID productId;

    @NotNull
    QuantityState quantityState;
}