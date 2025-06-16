package ru.yandex.practicum.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartDto {
    String shoppingCartId;
    Map<String, Long> products;
}
