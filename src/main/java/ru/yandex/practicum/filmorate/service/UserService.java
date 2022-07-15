package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j

public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", friendId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriendsList().add(friendId);
        log.info("Юзеру c id = \"{}\" добавлен друг c id = \"{}\"", userId, friendId);
        friend.getFriendsList().add(userId);
        log.info("Юзеру c id = \"{}\" добавлен друг c id = \"{}\"", friendId, userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", friendId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        for (User user : userStorage.getAllUsers()) {
            if (user.getId().equals(userId)) {
                user.getFriendsList().remove(friendId);
                log.info("У пользователя \"{}\" удален друг \"{}\"", userId, friendId);
            }
            if (user.getId().equals(friendId)) {
                user.getFriendsList().remove(userId);
                log.info("У пользователя c id = \"{}\" удален друг c id = \"{}\"", friendId, userId);
            }
        }
    }


    public List<User> getFriendsList(Long userId) {
        if (userStorage.getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        HashSet<Long> friendsIds = userStorage.getUserById(userId).getFriendsList();
        List<User> toReturn = new ArrayList<>();
        for (Long id : friendsIds) {
            toReturn.add(userStorage.getUserById(id));
        }
        log.info("Список друзей пользователя c id = \"{}\"", userId);
        return toReturn;
    }

    public List<User> getCommonFriendsList(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", friendId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        List<User> friendList = getFriendsList(friendId);
        List<User> userList = getFriendsList(userId);
        List<User> listToReturn = new ArrayList<>();
        for (User user : friendList) {
            if (userList.contains(user)) {
                listToReturn.add(user);
            }
        }
        log.info("Список общих друзей пользователей c id = \"{}\" и id = \"{}\"", userId, friendId);
        return listToReturn;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) throws ValidationException, UserNotFoundException {
        return userStorage.addUser(user);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public User updateUser(User user) throws ValidationException, UserNotFoundException {
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }
}
