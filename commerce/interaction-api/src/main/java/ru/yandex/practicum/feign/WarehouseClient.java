package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PutMapping
    void createProductInWarehouse(@RequestBody NewProductInWarehouseRequest request) throws FeignException;

    @PostMapping(value = "/check",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    BookedProductsDto checkShoppingCart(@Valid @RequestBody ShoppingCartDto shoppingCartDto) throws FeignException;

    @PostMapping("/add")
    void addProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequest request) throws FeignException;

    @GetMapping("/address")
    AddressDto getAddress() throws FeignException;

    @PostMapping("/return")
    void acceptReturn(@RequestBody Map<UUID, Long> returnedProducts) throws FeignException;

    @PostMapping("/assembly")
    BookedProductsDto assembleProducts(@Valid @RequestBody AssemblyProductsForOrderRequest request) throws FeignException;

    @PostMapping("/shipped")
    void shipToDelivery(@Valid @RequestBody ShippedToDeliveryRequest request) throws FeignException;
}