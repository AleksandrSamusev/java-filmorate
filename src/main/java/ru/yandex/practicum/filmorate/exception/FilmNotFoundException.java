package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    String message;

    public FilmNotFoundException(String message) {
        super(message);
    }
}
