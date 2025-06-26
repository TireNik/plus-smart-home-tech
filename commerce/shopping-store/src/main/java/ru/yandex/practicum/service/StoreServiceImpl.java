package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.exeption.ProductNotFoundException;
import ru.yandex.practicum.mapper.StoreMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.StoreRepository;
import ru.yandex.practicum.type.ProductCategory;
import ru.yandex.practicum.type.ProductState;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService{

    private final StoreRepository storeRepository;
    private final StoreMapper mapper;

    @Override
    public Page<ProductDto> getProductsByCategory(ProductCategory category, PageableDto pageable) {
        Pageable pageRequest = PageRequest.of(
                pageable.getPage(),
                pageable.getSize(),
                Sort.by(Sort.Direction.ASC, pageable.getSort().toArray(new String[0]))
        );

        Page<Product> page = storeRepository.findAllByProductCategory(category, pageRequest);

        return page.map(mapper::toProductDto);
    }

    @Transactional
    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = mapper.toProduct(productDto);
        product.setProductState(ProductState.ACTIVE);
        return mapper.toProductDto(storeRepository.save(product));
    }

    @Transactional
    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        getProductById(productDto.getProductId());
            return mapper.toProductDto(storeRepository.save(mapper.toProduct(productDto)));
    }

    @Transactional
    @Override
    public boolean removeProductFromStore(UUID productId) {
        Product product = getProductById(productId);
        product.setProductState(ProductState.DEACTIVATE);
        storeRepository.save(product);
        return false;
    }

    @Transactional
    @Override
    public boolean updateProductQuantityState(SetProductQuantityStateRequest request) {
        Product product = getProductById(request.getProductId());
        if (product.getQuantityState().equals(request.getQuantityState())) {
            return false;
        }
        product.setQuantityState(request.getQuantityState());
        storeRepository.save(product);
        return true;
    }

    @Override
    public ProductDto getInfoProductById(UUID productId) {
        Product product = getProductById(productId);
        return mapper.toProductDto(product);
    }

    private Product getProductById(UUID productId) {
        Optional<Product> product = Optional.ofNullable(storeRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар с таким productId не найден")));
        return product.get();
    }

}
