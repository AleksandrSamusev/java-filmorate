package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.support.UserIdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        userList.addAll(users.values());
        return userList;
    }



    @Override
    public User addUser(User user) throws ValidationException, UserNotFoundException {
        if (isValid(user) && !users.containsValue(user)) {
            user.setId(UserIdGenerator.generateId());
            users.put(user.getId(), user);
        }
        log.info("Добавлен пользователь, id = \"{}\"", user.getId());
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        log.info("Пользователь с id = \"{}\" удален", id);
        users.remove(id);
    }

    @Override
    public User updateUser(User user) throws ValidationException, UserNotFoundException {
        if (isValid(user)) {
            if (user.getId() != 0 && users.containsKey(user.getId())) {
                users.put(user.getId(), user);
            } else {
                user.setId(UserIdGenerator.generateId());
                users.put(user.getId(), user);
            }
        }
        log.info("Пользователь с id = \"{}\" обновлен", user.getId());
        return user;
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
        } else if (user.getId() < 0) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        log.info("Успешная валидация...");
        return true;
    }
}
