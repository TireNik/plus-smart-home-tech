package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order/")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderController implements OrderClient {
    OrderService orderService;

    @Override
    public List<OrderDto> getClientOrders(String username) throws FeignException {
        return orderService.getClientOrders(username);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) throws FeignException {
        return orderService.createNewOrder(request);
    }

    @Override
    public OrderDto assembly(UUID orderId) throws FeignException {
        return orderService.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) throws FeignException {
        return orderService.assemblyFailed(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) throws FeignException {
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) throws FeignException {
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    public OrderDto complete(UUID orderId) throws FeignException {
        return orderService.complete(orderId);
    }

    @Override
    public OrderDto delivery(UUID orderId) throws FeignException {
        return orderService.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) throws FeignException {
        return orderService.deliveryFailed(orderId);
    }

    @Override
    public OrderDto payment(UUID orderId) throws FeignException {
        return orderService.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) throws FeignException {
        return orderService.paymentFailed(orderId);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) throws FeignException {
        return orderService.productReturn(request);
    }
}

