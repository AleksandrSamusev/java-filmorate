package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.support.Constant;

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
        jdbcTemplate.update(Constant.QUERY_ADD_FRIEND, id, friendId);
        log.info("Юзеру c id = \"{}\" добавлен друг c id = \"{}\"", id, friendId);
    }

    public List<User> getFriendsList(Long id) {
        if (userDbStorage.getUserById(id) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        return jdbcTemplate.query(Constant.QUERY_GET_LIST_OF_FRIENDS, this::mapRowToUser, id);
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
        jdbcTemplate.update(Constant.QUERY_DELETE_FRIEND, id, friendId);
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
        log.info("Список общих друзей пользователей c id = \"{}\" и id = \"{}\"", id, friendId);
        return jdbcTemplate.query(Constant.QUERY_GET_COMMON_FRIENDS_LIST, this::mapRowToUser, id, friendId);
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
