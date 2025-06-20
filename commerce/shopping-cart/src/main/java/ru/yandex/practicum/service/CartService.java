package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.Map;

public interface CartService {
    ShoppingCartDto getShoppingCart(String userId);
    ShoppingCartDto addToShoppingCart(String userId, Map<String, Long> products);
    void deleteUserCart(String userId);
    ShoppingCartDto changeCart(String username, Map<String, Long> items);
    ShoppingCartDto changeCountProductInCart(String username, ChangeProductQuantityRequest request);
}
