package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final CartService cartService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String userId) {
        return cartService.getShoppingCart(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ShoppingCartDto addToShoppingCart(@RequestParam String userId,
                                             @RequestBody Map<String, Long> products) {
        return cartService.addToShoppingCart(userId, products);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public void deleteUserCart(@RequestParam String username) {
        cartService.deleteUserCart(username);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/remove")
    public ShoppingCartDto changeCart(@RequestParam String username,
                              @RequestBody Map<String, Long> items) {
        return cartService.changeCart(username, items);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeCountProductsOfCart(@RequestParam String username,
                                             @RequestBody ChangeProductQuantityRequest request) {
        return cartService.changeCountProductInCart(username, request);
    }
}
