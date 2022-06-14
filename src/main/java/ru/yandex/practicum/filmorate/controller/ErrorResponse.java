package ru.yandex.practicum.filmorate.controller;

public class ErrorResponse {
    String error;
    String description;

    public ErrorResponse(String error) {
        this.error = error;

    }

    public String getError() {
        return error;
    }
}
