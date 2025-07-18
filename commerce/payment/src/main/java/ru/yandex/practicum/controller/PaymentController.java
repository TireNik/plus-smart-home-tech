package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.feign.PaymentClient;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/payment")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PaymentController implements PaymentClient {
    PaymentService paymentService;


    @Override
    public Double getTotalCost(OrderDto orderDto) throws FeignException {
        return paymentService.getTotalCost(orderDto);
    }

    @Override
    public PaymentDto payment(OrderDto orderDto) throws FeignException {
        return paymentService.payment(orderDto);
    }

    @Override
    public void paymentFailed(UUID paymentId) throws FeignException {
        paymentService.paymentFailed(paymentId);
    }

    @Override
    public void paymentSuccess(UUID paymentId) throws FeignException {
        paymentService.paymentSuccess(paymentId);
    }

    @Override
    public Double productCost(OrderDto orderDto) throws FeignException {
        return paymentService.productCost(orderDto);
    }
}
