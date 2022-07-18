package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserDaoService;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserDaoService userDaoService;

    @Autowired
    public UserController(UserDaoService userDaoService) {
        this.userDaoService = userDaoService;
    }


    @GetMapping
    public List<User> getAllUsers() {
        return userDaoService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User userById(@PathVariable Long id) {
        return userDaoService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> listOfFriends(@PathVariable Long id) {
        return userDaoService.getFriendsList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> listOfCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
        return userDaoService.getCommonFriendsList(id, otherId);
    }

    @PostMapping
    public User addUser(@RequestBody User user) throws ValidationException {
        return userDaoService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        return userDaoService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId
    ) {
        userDaoService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable Long id,
            @PathVariable Long friendId
    ) {
        userDaoService.removeFriend(id, friendId);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id) {
        userDaoService.deleteUser(id);
    }
}
