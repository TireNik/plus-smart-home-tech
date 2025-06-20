package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.*;

public interface WarehouseService {
    void createProductInWarehouse(NewProductInWarehouseRequest newProduct);
    BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto);
    void addProductToWarehouse(AddProductToWarehouseRequest request);
    AddressDto getAddress();
}
