package ru.yandex.practicum.dto;

import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AddressDto {
    String country;
    String city;
    String street;
    String house;
    String flat;
}
