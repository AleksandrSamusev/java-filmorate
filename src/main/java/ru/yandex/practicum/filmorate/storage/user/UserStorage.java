package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User addUser(User user) throws ValidationException, UserNotFoundException;

    User updateUser(User user) throws ValidationException, UserNotFoundException;

    void deleteUser(Long id);
}
