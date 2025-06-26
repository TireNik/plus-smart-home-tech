package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.type.ProductCategory;

import java.util.UUID;

public interface StoreService {
    Page<ProductDto> getProductsByCategory(ProductCategory category, PageableDto pageableDto);
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(ProductDto productDto);
    boolean removeProductFromStore(UUID productId);
    boolean updateProductQuantityState(SetProductQuantityStateRequest request);
    ProductDto getInfoProductById(UUID productId);
}
