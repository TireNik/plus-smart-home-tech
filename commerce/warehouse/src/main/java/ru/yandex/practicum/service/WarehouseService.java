package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.NewProductInWarehouseRequest;

public interface WarehouseService {
    void createProductInWarehouse(NewProductInWarehouseRequest newProduct);
}
