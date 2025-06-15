package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
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
    public List<ProductDto> getProduct(@RequestParam ProductCategory category, PageableDto pageableDto) {
        return storeService.getProducts(category, pageableDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return storeService.updateProduct(productDto);
    }
}
