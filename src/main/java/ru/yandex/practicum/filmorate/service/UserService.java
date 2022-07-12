/*package ru.yandex.practicum.filmorate.service;

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
        if (getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (getUserById(friendId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", friendId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriendsList().add(friendId);
        log.info("Юзеру c id = \"{}\" добавлен друг c id = \"{}\"", userId, friendId);
        friend.getFriendsList().add(userId);
        log.info("Юзеру c id = \"{}\" добавлен друг c id = \"{}\"", friendId, userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (getUserById(friendId) == null) {
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

    public User getUserById(Long id) {
        if (id < 0) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
        for (User user : userStorage.getAllUsers()) {
            if (user.getId().equals(id)) {
                log.info("Вернулся пользователь c id = \"{}\"", user.getId());
                return user;
            }
        }
        log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", id);
        throw new UserNotFoundException("Пользователь не найден");
    }

    public List<User> getFriendsList(Long userId) {
        if (getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        HashSet<Long> friendsIds = getUserById(userId).getFriendsList();
        List<User> toReturn = new ArrayList<>();
        for (Long id : friendsIds) {
            toReturn.add(getUserById(id));
        }
        log.info("Список друзей пользователя c id = \"{}\"", userId);
        return toReturn;
    }

    public List<User> getCommonFriendsList(Long userId, Long friendId) {
        if (getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (getUserById(friendId) == null) {
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

}*/
