package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.exeption.payment.NoPaymentFoundException;
import ru.yandex.practicum.exeption.payment.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exeption.payment.PaymentStatus;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.feign.StoreClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PaymentServiceImpl implements PaymentService{

    PaymentMapper paymentMapper;
    PaymentRepository paymentRepository;
    OrderClient orderClient;
    StoreClient storeClient;

    @Value("${payment.fee.rate}")
    Double feeRate;

    @Override
    public Double getTotalCost(OrderDto orderDto) {
        checkCosts(orderDto);
        Payment payment = findPayment(orderDto.getPaymentId());
        Double totalCost = orderDto.getProductPrice() + orderDto.getProductPrice() * feeRate + orderDto.getDeliveryPrice();
        payment.setTotalPayment(totalCost);
        paymentRepository.save(payment);
        return totalCost;
    }

    @Override
    @Transactional
    public PaymentDto payment(OrderDto orderDto) {
        checkCosts(orderDto);
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .totalPayment(getTotalCost(orderDto))
                .deliveryTotal(orderDto.getDeliveryPrice())
                .feeTotal(orderDto.getTotalPrice() * feeRate)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        Payment payment = findPayment(paymentId);
        orderClient.paymentFailed(payment.getOrderId());
        payment.setPaymentStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        Payment payment = findPayment(paymentId);
        orderClient.payment(payment.getOrderId());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
    }

    @Override
    public Double productCost(OrderDto orderDto) {
        Map<UUID, Long> products = orderDto.getProducts();

        Map<UUID, Float> productPrices = products.keySet().stream()
                .map(storeClient::getProductById)
                .collect(Collectors.toMap(ProductDto::getProductId, ProductDto::getPrice));

        return products.entrySet().stream()
                .map(entry -> entry.getValue() * productPrices.get(entry.getKey()))
                .mapToDouble(Float::floatValue)
                .sum();
    }

    private void checkCosts(OrderDto orderDto) {
        if (orderDto.getTotalPrice() == null)
            throw new NotEnoughInfoInOrderToCalculateException("Отсутствует информация о стоимости заказа");
        if (orderDto.getDeliveryPrice() == null)
            throw new NotEnoughInfoInOrderToCalculateException("Отсутствует информация о стоимости доставки");
        if (orderDto.getProductPrice() == null)
            throw new NotEnoughInfoInOrderToCalculateException("Отсутствует информация о стоимости товаров");
    }

    private Payment findPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException("Платеж с id = " + paymentId + " не найден"));
    }
}
