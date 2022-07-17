package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class UserDaoService {

    private static final String QUERY_ADD_FRIEND = "insert into friendship (user_id, friend_id, is_confirmed) values (" +
            "?, ?, true);";

    private static final String QUERY_GET_LIST_OF_FRIENDS = "SELECT u.* FROM users AS u" +
            " JOIN friendship AS f ON u.user_id = f.friend_id" +
            " WHERE f.user_id = ?;";

    private static final String QUERY_DELETE_FRIEND = "delete from friendship where user_id = ? and friend_id = ?";

    private static final String QUERY_GET_COMMON_FRIENDS_LIST = "SELECT * FROM users" +
            " WHERE user_id IN (SELECT friend_id FROM friendship" +
            " WHERE user_id = ? AND is_confirmed IS true" +
            " OR user_id = ? AND is_confirmed IS true" +
            " GROUP by friend_id" +
            " HAVING count(friend_id) = 2);";

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    @Autowired
    public UserDaoService(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;

    }

    public User getUserById(Long id) {
        if (id < 0) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        String sqlQuery = "select * from users where user_id = ?";
        log.info("Вернулся пользователь c id = \"{}\"", id);
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
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
        if (getUserById(id) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", friendId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        jdbcTemplate.update(QUERY_ADD_FRIEND, id, friendId);
        log.info("Юзеру c id = \"{}\" добавлен друг c id = \"{}\"", id, friendId);
    }

    public List<User> getFriendsList(Long id) {
        if (getUserById(id) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        return jdbcTemplate.query(QUERY_GET_LIST_OF_FRIENDS, this::mapRowToUser, id);
    }

    public void removeFriend(Long id, Long friendId) {
        if (getUserById(id) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", friendId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        jdbcTemplate.update(QUERY_DELETE_FRIEND, id, friendId);
        log.info("У пользователя \"{}\" удален друг \"{}\"", id, friendId);
    }

    public List<User> getCommonFriendsList(Long id, Long friendId) {
        if (getUserById(id) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", friendId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        log.info("Список общих друзей пользователей c id = \"{}\" и id = \"{}\"", id, friendId);
        return jdbcTemplate.query(QUERY_GET_COMMON_FRIENDS_LIST, this::mapRowToUser, id, friendId);
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

}
