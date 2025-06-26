package ru.yandex.practicum.warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {
    @PostMapping(value = "/check",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    BookedProductsDto checkShoppingCart(@RequestBody ShoppingCartDto shoppingCartDto);
}
