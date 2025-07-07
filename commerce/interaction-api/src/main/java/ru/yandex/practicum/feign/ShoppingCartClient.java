package ru.yandex.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam("username") String username);

    @PutMapping
    ShoppingCartDto addToShoppingCart(@RequestParam("username") String username,
                                      @RequestBody Map<UUID, Long> products);

    @DeleteMapping
    void deleteUserCart(@RequestParam("username") String username);

    @PostMapping("/remove")
    ShoppingCartDto changeCart(@RequestParam("username") String username,
                               @RequestBody List<UUID> items);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeCountProductsOfCart(@RequestParam("username") String username,
                                              @RequestBody ChangeProductQuantityRequest request);
}