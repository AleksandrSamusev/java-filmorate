package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Qualifier
public class UserDbStorage implements UserStorage {

    private JdbcTemplate jdbcTemplate;

    public UserDbStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User addUser(User user) throws ValidationException, UserNotFoundException {
        return null;
    }

    @Override
    public User updateUser(User user) throws ValidationException, UserNotFoundException {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}
