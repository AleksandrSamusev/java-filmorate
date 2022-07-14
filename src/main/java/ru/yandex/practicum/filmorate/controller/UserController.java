package ru.yandex.practicum.filmorate.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserDbStorage userDbStorage;

    @Autowired
    public UserController(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    @GetMapping("/{id}")
    public User userById(@PathVariable Long id) {
        return userDbStorage.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> listOfFriends(@PathVariable Long id) {
        return userDbStorage.getFriendsList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> listOfCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
        return userDbStorage.getCommonFriendsList(id, otherId);
    }

    @PostMapping
    public User addUser(@RequestBody User user) throws ValidationException {
        return userDbStorage.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        return userDbStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId
    ) {
        userDbStorage.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable Long id,
            @PathVariable Long friendId
    ) {
        userDbStorage.removeFriend(id, friendId);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id) {
        userDbStorage.deleteUser(id);
    }
}
