package ru.yandex.practicum.filmorate.dao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SqlRowSet usersRows = jdbcTemplate.queryForRowSet("select * from users");

        if(usersRows.next()) {
            User user = new User(
                    usersRows.getLong("id"),
                    usersRows.getString("email"),
                    usersRows.getString("login"),
                    usersRows.getString("name"),
                    LocalDate.parse(usersRows.getString("birthday")));

            users.add(user);
        }
        return users;
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
        jdbcTemplate.update("DELETE * FROM users WHERE id=?", id);
    }
}
