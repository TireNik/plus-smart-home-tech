package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.feign.StoreClient;
import ru.yandex.practicum.service.StoreService;
import ru.yandex.practicum.type.ProductCategory;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class StoreController implements StoreClient {

    private final StoreService storeService;

    @Override
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<ProductDto> getProductByCategory(@RequestParam ProductCategory category, @Valid PageableDto pageableDto) {
        return storeService.getProductsByCategory(category, pageableDto);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        return storeService.createProduct(productDto);

    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return storeService.updateProduct(productDto);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/removeProductFromStore")
    public boolean removeProductFromStore(@RequestBody UUID productId) {
        return storeService.removeProductFromStore(productId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/quantityState")
    public boolean updateProductQuantityState(SetProductQuantityStateRequest request) {
        return storeService.updateProductQuantityState(request);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        return storeService.getInfoProductById(productId);
    }
}
