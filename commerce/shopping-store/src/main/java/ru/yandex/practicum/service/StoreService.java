package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.type.ProductCategory;

import java.util.List;

public interface StoreService {
    List<ProductDto> getProductsByCategory(ProductCategory category, PageableDto pageableDto);
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(ProductDto productDto);
    boolean removeProductFromStore(String productId);
    boolean updateProductQuantityState(SetProductQuantityStateRequest request);
    ProductDto getInfoProductById(String productId);
}
