package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderDto {
    @NotBlank
    UUID orderId;

    @NotBlank
    UUID shoppingCartId;
    Map<UUID, Long> products;
    UUID paymentId;
    UUID deliveryId;
    String state;
    Double deliveryWeight;
    Double deliveryVolume;
    boolean fragile;
    Double totalPrice;
    Double deliveryPrice;
    Double productPrice;
}
