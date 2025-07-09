package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exeption.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exeption.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exeption.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final OrderBookingRepository orderBookingRepository;

    @Override
    @Transactional
    public void createProductInWarehouse(NewProductInWarehouseRequest newProduct) {
        Optional<WarehouseProduct> product = warehouseRepository.findById(newProduct.getProductId());
        if (product.isPresent())
            throw new SpecifiedProductAlreadyInWarehouseException("Невозможно добавить товар, который уже есть в базе данных");
        warehouseRepository.save(warehouseMapper.toWarehouse(newProduct));
    }

    @Transactional
    @Override
    public BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto) {
        boolean hasFragile = false;
        double totalVolume = 0;
        double totalWeight = 0;

        for (Map.Entry<UUID, Long> entry : shoppingCartDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue();
            WarehouseProduct product = findProductById(productId);
            if (product.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Не хватает на складе товара с id = " + productId);
            }
            if (product.isFragile()) {
                hasFragile = true;
            }
            double productVolume = product.getDimension().getWidth()
                    * product.getDimension().getHeight()
                    * product.getDimension().getDepth();
            totalVolume += productVolume * requestedQuantity;
            totalWeight += product.getWeight() * requestedQuantity;
        }

        return BookedProductsDto.builder()
                .fragile(hasFragile)
                .deliveryVolume(totalVolume)
                .deliveryWeight(totalWeight)
                .build();
    }

    @Transactional
    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = findProductById(request.getProductId());
        product.setQuantity(product.getQuantity() + request.getQuantity());
        warehouseRepository.save(product);
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

    @Override
    public void shipToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking orderBooking = orderBookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + request.getOrderId()));
        orderBooking.setDeliveryId(request.getDeliveryId());
        orderBookingRepository.save(orderBooking);
    }

    @Override
    @Transactional
    public void acceptReturn(Map<UUID, Long> returnedProducts) {
        Map<UUID, WarehouseProduct> products = warehouseRepository.findAllById(returnedProducts.keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        for (Map.Entry<UUID, Long> entry : returnedProducts.entrySet()) {
            if (!products.containsKey(entry.getKey())) {
                throw new NotFoundException("Товар с id = " + entry.getKey() + " не найден на складе");
            }
            WarehouseProduct warehouseProduct = products.get(entry.getKey());
            warehouseProduct.setQuantity(warehouseProduct.getQuantity() + entry.getValue());
        }
        warehouseRepository.saveAll(products.values());
    }

    @Override
    @Transactional
    public BookedProductsDto assembleProducts(AssemblyProductsForOrderRequest request) {
        ShoppingCartDto shoppingCart = ShoppingCartDto.builder()
                .shoppingCartId(request.getOrderId())
                .products(request.getProducts())
                .build();
        BookedProductsDto bookedProductsDto = checkShoppingCart(shoppingCart);
        OrderBooking orderBooking = OrderBooking.builder()
                .orderId(request.getOrderId())
                .products(request.getProducts())
                .build();
        orderBookingRepository.save(orderBooking);
        return bookedProductsDto;
    }

    private WarehouseProduct findProductById(UUID productId) {
        return warehouseRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Не найден на складе товар с id = " + productId));
    }
}
