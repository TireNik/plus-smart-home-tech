package ru.yandex.practicum.exeption.payment;

public class NoPaymentFoundException extends RuntimeException{
    public NoPaymentFoundException(String message) {
        super(message);
    }
}
