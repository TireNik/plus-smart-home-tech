package ru.yandex.practicum.exeption.order;

public class NoSpecifiedProductInWarehouseException extends RuntimeException{
    public NoSpecifiedProductInWarehouseException(String message) {
        super(message);
    }
}
