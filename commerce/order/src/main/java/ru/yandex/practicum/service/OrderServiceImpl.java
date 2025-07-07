package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.exeption.NotAuthorizedUserException;
import ru.yandex.practicum.exeption.order.NoOrderFoundException;
import ru.yandex.practicum.feign.ShoppingCartClient;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderServiceImpl implements OrderService {

    OrderMapper mapper;
    OrderRepository repository;
    ShoppingCartClient shoppingCartClient;
    WarehouseClient warehouseClient;
    @Override
    public List<OrderDto> getClientOrders(String username) {
        checkUser(username);
        ShoppingCartDto shoppingCartDto = shoppingCartClient.getShoppingCart(username);

        return repository.findByShoppingCartId(shoppingCartDto.getShoppingCartId()).stream()
                .map(mapper::toDto)
                .toList();
    }

    private void checkUser(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Пользователь не авторизован");
        }

    }

    // todo:
    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {

        Order order = Order.builder()
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .build();

        BookedProductsDto bookedProductsDto = warehouseClient.checkShoppingCart(request.getShoppingCart());



        return null;
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.ASSEMBLED);
        return mapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = findOrder(orderId);
        return null;
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return mapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = findOrder(orderId);

        return null;
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.PAID);
        return mapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {

        return null;
    }

    private Order findOrder(UUID orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ с id = " + orderId));
    }
}