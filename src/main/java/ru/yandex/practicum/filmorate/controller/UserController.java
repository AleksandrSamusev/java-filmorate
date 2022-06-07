package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private static Integer userIdCounter = 0;
    private Map<Integer, User> users = new HashMap<>();

    public static void setUserIdCounter(Integer userIdCounter) {
        UserController.userIdCounter = userIdCounter;
    }

    @GetMapping
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        userList.addAll(users.values());
        return userList;

    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        if (isValid(user)) {
            user.setId(++userIdCounter);
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь: {}", user.getName());
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (isValid(user)) {
            if (user.getId() != 0 && users.containsKey(user.getId())) {
                users.put(user.getId(), user);
                log.info("Параметры пользователя обновлены: {}", user.getName());
            } else {
                user.setId(++userIdCounter);
                users.put(user.getId(), user);
                log.info("Добавлен новый пользователь: {}", user.getName());
            }
        }
        return user;
    }

    private boolean isValid(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("ValidationException: некорректно указана электронная почта.");
            throw new ValidationException("Некорректно указана электронная почта.");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.info("ValidationException: некорректно указан логин.");
            throw new ValidationException("Некорректно указан логин.");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("ValidationException: некорректно указана дата рождения.");
            throw new ValidationException("Некорректно указана дата рождения.");
        } else if (user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("в поле user.name записано значение поля user.login");
        } else if (user.getId() < 0) {
            log.info("ValidationException: передан отрицательный id");
            throw new ValidationException("Передан отрицательный id");
        }
        return true;
    }
}
