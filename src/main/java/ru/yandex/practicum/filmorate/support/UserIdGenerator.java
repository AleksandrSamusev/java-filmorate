package ru.yandex.practicum.filmorate.support;

public class UserIdGenerator {
    private static long id = 1;

    public static void setId(long id) {
        UserIdGenerator.id = id;
    }

    public static long generateId() {
        return id++;
    }
}
