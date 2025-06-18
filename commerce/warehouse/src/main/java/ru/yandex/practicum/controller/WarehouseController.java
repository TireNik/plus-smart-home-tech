package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;
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

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request) {
        warehouseService.addProductToWarehouse(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/address")
    public AddressDto getAddress() {
        return warehouseService.getAddress();
    }
}
