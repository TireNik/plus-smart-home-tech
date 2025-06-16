package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService{

    private final StoreRepository storeRepository;
    private final StoreMapper mapper;

    @Override
    public List<ProductDto> getProductsByCategory(ProductCategory category, PageableDto pageable) {
        Pageable page = PageRequest.of(pageable.getPage(), pageable.getSize(),
        Sort.by(Sort.DEFAULT_DIRECTION, String.join(",", pageable.getSort())));

        List<Product> products = storeRepository.findAllByProductCategory(category, page);

        return mapper.toProductDtoList(products);
    }

    @Transactional
    @Override
    public ProductDto createProduct(ProductDto productDto) {
        if (storeRepository.findByProductId(productDto.getProductId()).isEmpty()) {
            throw new ProductNotFoundException("Создаваемый товар уже есть в базе данных");
        }
        Product product = mapper.toProduct(productDto);
        return mapper.toProductDto(storeRepository.save(product));
    }

    @Transactional
    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = getProductById(productDto.getProductId());
            return mapper.toProductDto(storeRepository.save(product));
    }

    @Transactional
    @Override
    public boolean removeProductFromStore(String productId) {
        Product product = getProductById(productId);
        product.setProductState(ProductState.DEACTIVATE);
        storeRepository.save(product);
        return false;
    }

    @Transactional
    @Override
    public boolean updateProductQuantityState(SetProductQuantityStateRequest request) {
        Product product = getProductById(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        storeRepository.save(product);
        return false;
    }

    @Override
    public ProductDto getInfoProductById(String productId) {
        Product product = getProductById(productId);
        return mapper.toProductDto(product);
    }

    private Product getProductById(String productId) {
        Optional<Product> product = Optional.ofNullable(storeRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар с таким productId не найден")));
        return product.get();
    }

}
