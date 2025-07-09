package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.util.UUID;

@FeignClient(name = "payment", path = "api/v1/payment")
public interface PaymentClient {
    @PostMapping("/totalCost")
    Double getTotalCost(@Valid @RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping
    PaymentDto payment(@Valid @RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/failed")
    void paymentFailed(@RequestParam UUID paymentId) throws FeignException;

    @PostMapping("/refund")
    void paymentSuccess(@RequestParam UUID paymentId) throws FeignException;

    @PostMapping("/productCost")
    Double productCost(@Valid @RequestBody OrderDto orderDto) throws FeignException;

}
