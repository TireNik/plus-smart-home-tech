package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

public interface WarehouseService {
    void createProductInWarehouse(NewProductInWarehouseRequest newProduct);
    BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto);
}
