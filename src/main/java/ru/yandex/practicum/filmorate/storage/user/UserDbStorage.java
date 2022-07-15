package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
public class UserDbStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUserById(Long id) {
        if (id < 0) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        String sqlQuery = "select * from users where user_id = ?";
        log.info("Вернулся пользователь c id = \"{}\"", id);
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
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

    @Override
    public User addUser(User user) throws ValidationException {
        if (isValid(user)) {
            String sqlQuery = "insert into users(login, name, email, birthday) " +
                    "values (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
                stmt.setString(1, user.getLogin());
                stmt.setString(2, user.getName());
                stmt.setString(3, user.getEmail());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);
            user.setId(keyHolder.getKey().longValue());
            return user;
        }

        log.info("ValidationException:" +
                " валидация пользователя c id = \"{}\" не прошла", user.getId());
        throw new ValidationException("Валидация пользователя не прошла");
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        if (isValid(user) && user.getId() > 0) {
            String sqlQuery = "update users set " +
                    "login = ?, name = ?, email = ?, birthday = ? " +
                    "where user_id = ?";
            jdbcTemplate.update(sqlQuery
                    , user.getLogin()
                    , user.getName()
                    , user.getEmail()
                    , user.getBirthday()
                    , user.getId());
            return user;
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public void deleteUser(Long id) {
        String sqlQuery = "delete from users where user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private boolean isValid(User user) throws ValidationException, UserNotFoundException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("ValidationException: некорректно указана электронная почта");
            throw new ValidationException("Некорректно указана электронная почта.");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.info("ValidationException: некорректно указан логин");
            throw new ValidationException("Некорректно указан логин.");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("ValidationException: некорректно указана дата рождения");
            throw new ValidationException("Некорректно указана дата рождения.");
        } else if (user.getName().isBlank()) {
            log.info("Пользователь \"{}\" не передал имя, устанавливаю поле Name = Login", user.getId());
            user.setName(user.getLogin());
        }
        log.info("Успешная валидация...");
        return true;
    }
}
