package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exeption.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exeption.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exeption.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    public void createProductInWarehouse(NewProductInWarehouseRequest newProduct) {
        Optional<WarehouseProduct> product = warehouseRepository.findById(newProduct.getProductId());
        if (product.isPresent())
            throw new SpecifiedProductAlreadyInWarehouseException("Невозможно добавить товар, который уже есть в базе данных");
        warehouseRepository.save(warehouseMapper.toWarehouse(newProduct));
    }

    @Transactional
    @Override
    public BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto) {
        Map<String, Long> products = shoppingCartDto.getProducts();
        List<WarehouseProduct> warehouseProducts = warehouseRepository.findAllById(products.keySet());
        warehouseProducts.forEach(product -> {
            if (product.getQuantity() <= products.get(product.getProductId())) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Невозможно оформить заказ, недостаточно товара");
            }
        });

        double deliveryWeight = warehouseProducts.stream()
                .map(WarehouseProduct::getWeight)
                .mapToDouble(Double::doubleValue)
                .sum();

        double deliveryVolume = warehouseProducts.stream()
                .map(warehouseProduct -> warehouseProduct.getDimension().getDepth()
                        * warehouseProduct.getDimension().getWidth() * warehouseProduct.getDimension().getHeight())
                .mapToDouble(Double::doubleValue)
                .sum();

        boolean fragile = warehouseProducts.stream()
                .anyMatch(WarehouseProduct::isFragile);

        return BookedProductsDto.builder()
                .deliveryWeight(deliveryWeight)
                .deliveryVolume(deliveryVolume)
                .fragile(fragile)
                .build();
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        Optional<WarehouseProduct> product = warehouseRepository.findById(request.getProductId());
        if (product.isPresent()) {
            throw new NoSpecifiedProductInWarehouseException("Товар не найден");
        }
        WarehouseProduct warehouseProduct = product.get();
        warehouseProduct.setQuantity(warehouseProduct.getQuantity() + request.getQuantity());
        warehouseRepository.save(warehouseProduct);
    }

    @Override
    public AddressDto getAddress() {
        return AddressDto.builder()
                .country("Россия")
                .city("Москва")
                .street("Ленинградский проспект")
                .house("10")
                .flat("1")
                .build();
    }
}
