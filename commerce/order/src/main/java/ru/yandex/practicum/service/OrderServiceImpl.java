package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.exeption.NotAuthorizedUserException;
import ru.yandex.practicum.exeption.order.NoOrderFoundException;
import ru.yandex.practicum.feign.DeliveryClient;
import ru.yandex.practicum.feign.PaymentClient;
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

    final OrderMapper mapper;
    final OrderRepository repository;
    final ShoppingCartClient shoppingCartClient;
    final WarehouseClient warehouseClient;
    final DeliveryClient deliveryClient;
    final PaymentClient paymentClient;

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
        repository.save(order);

        AssemblyProductsForOrderRequest assemblyRequest = AssemblyProductsForOrderRequest.builder()
                .orderId(order.getOrderId())
                .products(request.getShoppingCart().getProducts())
                .build();
        BookedProductsDto bookedProductsDto = warehouseClient.assembleProducts(assemblyRequest);

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(order.getOrderId())
                .fromAddress(warehouseClient.getAddress())
                .toAddress(request.getDeliveryAddress())
                .deliveryState(DeliveryState.CREATED)
                .build();
        deliveryDto = deliveryClient.planDelivery(deliveryDto);

        PaymentDto paymentDto = paymentClient.payment(mapper.toDto(order));

        order.setPaymentId(paymentDto.getPaymentId());
        order.setDeliveryId(deliveryDto.getDeliveryId());
        order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
        order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
        order.setFragile(bookedProductsDto.getFragile());
        order.setTotalPrice(paymentClient.getTotalCost(mapper.toDto(order)));
        order.setDeliveryPrice(deliveryClient.deliveryCost(mapper.toDto(order)));
        order.setProductPrice(paymentClient.productCost(mapper.toDto(order)));

        return mapper.toDto(repository.save(order));
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
        order.setDeliveryPrice(deliveryClient.deliveryCost(mapper.toDto(order)));
        return mapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = findOrder(orderId);
        order.setTotalPrice(paymentClient.getTotalCost(mapper.toDto(order)));
        return mapper.toDto(repository.save(order));
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
        deliveryClient.deliverySuccessful(orderId);
        order.setState(OrderState.DELIVERED);
        return mapper.toDto(repository.save(order));
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
        Order order = findOrder(request.getOrderId());
        warehouseClient.acceptReturn(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        return mapper.toDto(repository.save(order));
    }

    private Order findOrder(UUID orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ с id = " + orderId));
    }
}