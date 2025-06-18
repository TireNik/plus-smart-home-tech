package ru.yandex.practicum.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ShoppingCartDto {
    String shoppingCartId;
    Map<String, Long> products;
}
