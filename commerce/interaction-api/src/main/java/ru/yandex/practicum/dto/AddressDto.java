package ru.yandex.practicum.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
public class AddressDto {
    String country;
    String city;
    String street;
    String house;
    String flat;
}
