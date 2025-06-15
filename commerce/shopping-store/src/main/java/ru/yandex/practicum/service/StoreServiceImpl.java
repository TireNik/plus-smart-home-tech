package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.exeption.ProductNotFoundException;
import ru.yandex.practicum.mapper.StoreMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.StoreRepository;
import ru.yandex.practicum.type.ProductCategory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService{

    private final StoreRepository storeRepository;
    private final StoreMapper mapper;

    @Override
    public List<ProductDto> getProducts(ProductCategory category, PageableDto pageable) {
        Pageable page = PageRequest.of(pageable.getPage(), pageable.getSize(),
        Sort.by(Sort.DEFAULT_DIRECTION, String.join(",", pageable.getSort())));

        List<Product> products = storeRepository.findAllByProductCategory(category, page);

        return mapper.toProductDtoList(products);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        if (storeRepository.findByProductId(productDto.getProductId()).isEmpty()) {
            throw new ProductNotFoundException("Создаваемый товар уже есть в базе данных");
        }
        Product product = mapper.toProduct(productDto);
        return mapper.toProductDto(storeRepository.save(product));
    }


}
