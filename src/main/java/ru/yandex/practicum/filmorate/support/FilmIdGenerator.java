package ru.yandex.practicum.filmorate.support;

public class FilmIdGenerator {
    private static long id = 1;

    public static void setId(long id) {
        FilmIdGenerator.id = id;
    }

    public static long generateId() {
        return id++;
    }
}
