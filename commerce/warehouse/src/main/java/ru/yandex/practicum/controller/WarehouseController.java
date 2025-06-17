package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;


    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public void createProductInWarehouse(@RequestBody NewProductInWarehouseRequest request) {
        warehouseService.createProductInWarehouse(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/check")
    public BookedProductsDto checkShoppingCart(@RequestBody ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkShoppingCart(shoppingCartDto);
    }
}
