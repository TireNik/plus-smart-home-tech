package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.StoreService;
import ru.yandex.practicum.type.ProductCategory;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class StoreController {

    private final StoreService storeService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ProductDto> getProductByCategory(@RequestParam ProductCategory category, PageableDto pageableDto) {
        return storeService.getProductsByCategory(category, pageableDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        return storeService.createProduct(productDto);

    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return storeService.updateProduct(productDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/removeProductFromStore")
    public boolean removeProductFromStore(@RequestBody String productId) {
        return storeService.removeProductFromStore(productId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/quantityState")
    public boolean updateProductQuantityState(@RequestBody SetProductQuantityStateRequest request) {
        return storeService.updateProductQuantityState(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{productId}")
    public ProductDto getProductById(@PathVariable String productId) {
        return storeService.getInfoProductById(productId);
    }
}
