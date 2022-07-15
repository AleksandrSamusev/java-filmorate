package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class UserDaoService {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    @Autowired
    public UserDaoService(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;

    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    public void addFriend(Long id, Long friendId) {
        if (userDbStorage.getUserById(id) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (userDbStorage.getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", friendId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        String sqlQuery = "insert into friendship (user_id, friend_id, is_confirmed) values (?, ?, true);";
        jdbcTemplate.update(sqlQuery, id, friendId);
        log.info("Юзеру c id = \"{}\" добавлен друг c id = \"{}\"", id, friendId);
    }

    public List<User> getFriendsList(Long id) {
        if (userDbStorage.getUserById(id) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        String sqlQuery = "SELECT u.* FROM users AS u" +
                " JOIN friendship AS f ON u.user_id = f.friend_id" +
                " WHERE f.user_id = ?;";
        log.info("Список друзей пользователя c id = \"{}\"", id);
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    public void removeFriend(Long id, Long friendId) {
        if (userDbStorage.getUserById(id) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (userDbStorage.getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", friendId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        String sqlQuery = "delete from friendship where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
        log.info("У пользователя \"{}\" удален друг \"{}\"", id, friendId);
    }

    public List<User> getCommonFriendsList(Long id, Long friendId) {
        if (userDbStorage.getUserById(id) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (userDbStorage.getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", friendId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        String sqlQuery = "SELECT * FROM users" +
                " WHERE user_id IN (SELECT friend_id FROM friendship" +
                " WHERE user_id = ? AND is_confirmed IS true" +
                " OR user_id = ? AND is_confirmed IS true" +
                " GROUP by friend_id" +
                " HAVING count(friend_id) = 2);";

        log.info("Список общих друзей пользователей c id = \"{}\" и id = \"{}\"", id, friendId);
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, friendId);
    }

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public User addUser(User user) throws ValidationException {
        return userDbStorage.addUser(user);
    }

    public User updateUser(User user) throws ValidationException {
        return userDbStorage.updateUser(user);
    }

    public void deleteUser(Long id) {
        userDbStorage.deleteUser(id);
    }

    public User getUserById(Long id) {
        return userDbStorage.getUserById(id);
    }
}
