package ru.yandex.practicum.filmorate.support;

import java.time.LocalDate;

public class Constant {
    private static final LocalDate THE_BORN_OF_CINEMATOGRAPHY = LocalDate.of(1895, 12, 28);

    public static LocalDate getTheBornOfCinematography() {
        return THE_BORN_OF_CINEMATOGRAPHY;
    }
}
