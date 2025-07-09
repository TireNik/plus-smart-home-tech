package ru.yandex.practicum.exeption.delivery;

public class NoDeliveryFoundException extends RuntimeException{
    public NoDeliveryFoundException(String message) {
        super(message);
    }
}
