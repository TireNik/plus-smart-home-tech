package ru.yandex.practicum.exeption.order;

public class NotAuthorizedUserException extends RuntimeException{
    public NotAuthorizedUserException(String message) {
        super(message);
    }
}
