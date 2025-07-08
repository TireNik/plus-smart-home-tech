package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.exeption.delivery.NoDeliveryFoundException;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryServiceImpl implements DeliveryService{

    DeliveryRepository deliveryRepository;
    DeliveryMapper deliveryMapper;
    WarehouseClient warehouseClient;
    OrderClient orderClient;

    @Value("${delivery.base_rate}")
    Double baseRate;

    @Value("${delivery.address_rate}")
    Integer addressRate;

    @Value("${delivery.fragile_rate}")
    Double fragileRate;

    @Value("${delivery.weight_rate}")
    Double weightRate;

    @Value("${delivery.volume_rate}")
    Double volumeRate;

    @Value("${delivery.street_rate}")
    Double streetRate;

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toEntity(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = findDelivery(orderId);
        orderClient.complete(delivery.getOrderId());
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = findDelivery(orderId);
        orderClient.assembly(delivery.getOrderId());
        ShippedToDeliveryRequest request = ShippedToDeliveryRequest.builder()
                .orderId(delivery.getOrderId())
                .deliveryId(delivery.getDeliveryId())
                .build();
        warehouseClient.shipToDelivery(request);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID orderId) {
        Delivery delivery = findDelivery(orderId);
        orderClient.deliveryFailed(delivery.getOrderId());
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public Double deliveryCost(OrderDto orderDto) {
        Delivery delivery = findDelivery(orderDto.getDeliveryId());
        AddressDto address = warehouseClient.getAddress();
        Double deliveryCost = baseRate;

        if (address.getStreet().equals("ADDRESS_1")) {
            deliveryCost += deliveryCost;
        } else if (address.getStreet().equals("ADDRESS_2")) {
            deliveryCost += deliveryCost * addressRate;
        }

        if (orderDto.isFragile()) deliveryCost += deliveryCost * fragileRate;

        deliveryCost += orderDto.getDeliveryWeight() * weightRate;
        deliveryCost += orderDto.getDeliveryVolume() * volumeRate;

        if (delivery.getToAddress().getStreet().equals(address.getStreet())) deliveryCost += deliveryCost * streetRate;

        return deliveryCost;
    }

    private Delivery findDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка с id = " + deliveryId + " не найдена"));
    }
}
