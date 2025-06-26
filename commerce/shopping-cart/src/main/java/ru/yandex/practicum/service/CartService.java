package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartService {
    ShoppingCartDto getShoppingCart(String userId);
    ShoppingCartDto addToShoppingCart(String userId, Map<UUID, Long> products);
    void deleteUserCart(String userId);
    ShoppingCartDto changeCart(String username, List<UUID> items);
    ShoppingCartDto changeCountProductInCart(String username, ChangeProductQuantityRequest request);
}
