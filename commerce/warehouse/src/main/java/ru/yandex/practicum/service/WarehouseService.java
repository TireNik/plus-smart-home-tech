package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void createProductInWarehouse(NewProductInWarehouseRequest newProduct);
    BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto);
    void addProductToWarehouse(AddProductToWarehouseRequest request);
    AddressDto getAddress();
    void shipToDelivery(ShippedToDeliveryRequest request);
    void acceptReturn(Map<UUID, Long> returnedProducts);
    BookedProductsDto assembleProducts(AssemblyProductsForOrderRequest request);

}
