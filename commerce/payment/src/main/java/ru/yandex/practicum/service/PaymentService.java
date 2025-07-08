package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    Double getTotalCost(OrderDto orderDto);
    PaymentDto payment(OrderDto orderDto);
    void paymentFailed(UUID paymentId);
    void paymentSuccess(UUID paymentId);
    Double productCost(OrderDto orderDto);
}
