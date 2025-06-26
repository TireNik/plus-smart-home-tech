package ru.yandex.practicum.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ShoppingCartDto {
    String shoppingCartId;
    Map<String, Long> products;
}
