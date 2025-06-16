package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    public void createProductInWarehouse(NewProductInWarehouseRequest newProduct) {
        Optional<WarehouseProduct> product = warehouseRepository.findById(newProduct.getProductId());
        if (product.isPresent())
            throw new Добавить("Невозможно добавить товар, который уже есть в базе данных");
        warehouseRepository.save(warehouseMapper.toWarehouse(newProduct));
    }
}
