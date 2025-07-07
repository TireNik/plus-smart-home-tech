package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
@Slf4j
public class WarehouseController implements WarehouseClient {

    private final WarehouseService warehouseService;

    @Override
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public void createProductInWarehouse(@RequestBody NewProductInWarehouseRequest request) {
        warehouseService.createProductInWarehouse(request);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/check",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BookedProductsDto checkShoppingCart(@RequestBody ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkShoppingCart(shoppingCartDto);
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request) {
        warehouseService.addProductToWarehouse(request);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/address")
    public AddressDto getAddress() {
        return warehouseService.getAddress();
    }

    @Override
    public void acceptReturn(Map<UUID, Long> returnedProducts) throws FeignException {

    }

    @Override
    public BookedProductsDto assembleProducts(AssemblyProductsForOrderRequest request) throws FeignException {
        return null;
    }

    @Override
    public void shipToDelivery(ShippedToDeliveryRequest request) throws FeignException {

    }
}
