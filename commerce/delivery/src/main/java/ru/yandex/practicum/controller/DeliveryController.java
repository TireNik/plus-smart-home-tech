package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.feign.DeliveryClient;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/delivery")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DeliveryController implements DeliveryClient {
    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) throws FeignException {
        return null;
    }

    @Override
    public void deliverySuccessful(UUID orderId) throws FeignException {

    }

    @Override
    public void deliveryPicked(UUID orderId) throws FeignException {

    }

    @Override
    public void deliveryFailed(UUID orderId) throws FeignException {

    }

    @Override
    public Double deliveryCost(OrderDto orderDto) throws FeignException {
        return 0.0;
    }
}
