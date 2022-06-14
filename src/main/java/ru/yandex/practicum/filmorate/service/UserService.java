package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
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
    UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        if (getUserById(userId) == null || getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь не найден");
            throw new UserNotFoundException("Пользователь не найден");
        }
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriendsList().add(friendId);
        log.info("Юзеру \"{}\" добавлен друг \"{}\"", userId, friendId);
        friend.getFriendsList().add(userId);
        log.info("Юзеру \"{}\" добавлен друг \"{}\"", friendId, userId);

    }

    public void removeFriend(Long userId, Long friendId) {
        if (getUserById(userId) == null || getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь не найден");
            throw new UserNotFoundException("Пользователь не найден");
        }
        for (User user : userStorage.getAllUsers()) {
            if (user.getId().equals(userId)) {
                user.getFriendsList().remove(friendId);
                log.info("У пользователя \"{}\" удален друг \"{}\"", userId, friendId);
            }
            if (user.getId().equals(friendId)) {
                user.getFriendsList().remove(userId);
                log.info("У пользователя \"{}\" удален друг \"{}\"", friendId, userId);
            }
        }
    }

    public User getUserById(Long id) {
        if (id < 0) {
            log.info("UserNotFoundException: пользователь не найден");
            throw new UserNotFoundException("Пользователь не найден");
        }
        for (User user : userStorage.getAllUsers()) {
            if (user.getId().equals(id)) {
                log.info("Вернулся пользователь \"{}\"", user.getId());
                return user;
            }
        }
        log.info("Пользователя с id = \"{}\" не найден", id);
        return null;
    }

    public List<User> getFriendsList(Long userId) {
        if (getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь не найден");
            throw new UserNotFoundException("Пользователь не найден");
        }
        HashSet<Long> friendsIds = getUserById(userId).getFriendsList();
        List<User> toReturn = new ArrayList<>();
        for (Long id : friendsIds) {
            toReturn.add(getUserById(id));
        }
        log.info("Список друзей пользователя \"{}\"", userId);
        return toReturn;
    }

    public List<User> getCommonFriendsList(Long userId, Long friendId) {
        if (getUserById(userId) == null || getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь не найден");
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
        log.info("Список общих друзей пользователей \"{}\" и \"{}\"", userId, friendId);
        return listToReturn;
    }

}
