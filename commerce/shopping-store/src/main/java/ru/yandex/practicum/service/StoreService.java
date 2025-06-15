package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.type.ProductCategory;

import java.util.List;

public interface StoreService {
    List<ProductDto> getProducts(ProductCategory category, PageableDto pageableDto);
}
