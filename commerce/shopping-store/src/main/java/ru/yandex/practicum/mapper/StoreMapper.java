package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.model.Product;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StoreMapper {

    Product toProduct(ProductDto productDto);

    ProductDto toProductDto(Product product);
}
