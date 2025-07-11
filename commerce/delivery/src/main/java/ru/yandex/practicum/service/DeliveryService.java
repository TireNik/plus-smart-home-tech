package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto planDelivery(DeliveryDto deliveryDto);
    void deliverySuccessful(UUID orderId);
    void deliveryPicked(UUID orderId);
    void deliveryFailed(UUID orderId);
    Double deliveryCost(OrderDto orderDto);
}
