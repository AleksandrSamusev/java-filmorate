package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.support.FilmIdGenerator;
import ru.yandex.practicum.filmorate.support.UserIdGenerator;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {

    @Test
    void contextLoads() {
    }

    @BeforeEach
    @Test
    void resetId() {
        FilmIdGenerator.setId(1);
        UserIdGenerator.setId(1);
    }

    @Test
    void getListOfUsers() {
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        Assertions.assertEquals(0, userController.getAllUsers().size(), "Wrong size");
    }

    @Test
    void addNewUserWithAllGoodParameters() throws ValidationException {
        User user = new User("Niko123", "Nikolas", "niko123@nikolas.lol",
                LocalDate.of(2000, 1, 1));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user);
        Assertions.assertEquals(1, userController.getAllUsers().size(), "Wrong size");
        Assertions.assertEquals(user, userController.getAllUsers().get(0), "Wrong user");
    }

    @Test
    void addNewUserWithEmptyNameField() throws ValidationException {
        User user = new User("Sveta_123", "", "Sveta_123@nikolas.lol",
                LocalDate.of(1965, 12, 12));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user);
        Assertions.assertEquals(1, userController.getAllUsers().size(), "Wrong size");
        Assertions.assertEquals(user.getName(), userController.getAllUsers().get(0).getLogin(),
                "The name and login do not match!");
    }

    @Test
    void addNewUserWithEmptyLoginField() throws ValidationException {
        User user = new User("", "Karlos", "karlos@Omega.lul",
                LocalDate.of(1999, 9, 4));

        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        Assertions.assertEquals("Некорректно указан логин.", thrown.getMessage());
        Assertions.assertEquals(0, userController.getAllUsers().size(), "Wrong size!");
    }

    @Test
    void addNewUserWithEmptyEmailField() throws ValidationException {
        User user = new User("Nikolai_666", "Niko", "",
                LocalDate.of(1912, 1, 1));

        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        Assertions.assertEquals("Некорректно указана электронная почта.", thrown.getMessage());
        Assertions.assertEquals(0, userController.getAllUsers().size(), "Wrong size!");
    }

    @Test
    void addNewUserWithout_AT_symbolInEmailAdress() {
        User user = new User("Tamara_321", "Toma", "toma_Toma.lol",
                LocalDate.of(1912, 1, 1));

        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        Assertions.assertEquals("Некорректно указана электронная почта.", thrown.getMessage());
        Assertions.assertEquals(0, userController.getAllUsers().size(), "Wrong size!");
    }

    @Test
    void addNewUserWithOneSpaceInLoginField() {
        User user = new User("Tamara 321", "Toma", "toma@Toma.lol",
                LocalDate.of(1912, 1, 1));

        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        Assertions.assertEquals("Некорректно указан логин.", thrown.getMessage());
        Assertions.assertEquals(0, userController.getAllUsers().size(), "Wrong size!");
    }

    @Test
    void addNewUserWithThreeSpacesInLoginField() {
        User user = new User("Tamara   321", "Toma", "toma@Toma.lol",
                LocalDate.of(1912, 1, 1));

        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        Assertions.assertEquals("Некорректно указан логин.", thrown.getMessage());
        Assertions.assertEquals(0, userController.getAllUsers().size(), "Wrong size!");
    }

    @Test
    void addNewUserWithBirthdayFieldInFuture() {
        User user = new User("Sergey_O", "Serega", "sergey@goodguy.moc",
                LocalDate.of(2030, 1, 1));

        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        Assertions.assertEquals("Некорректно указана дата рождения.", thrown.getMessage());
        Assertions.assertEquals(0, userController.getAllUsers().size(), "Wrong size!");
    }

    @Test
    void addNewUserWithBirthdayFieldInPast() throws ValidationException {
        User user = new User("Sergey_O", "Serega", "sergey@goodguy.moc",
                LocalDate.of(2010, 1, 1));

        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

        userController.addUser(user);

        Assertions.assertEquals(1, userController.getAllUsers().size(), "Wrong size!");
        Assertions.assertEquals("Serega", userController.getAllUsers().get(0).getName(),
                "Wrong name");
    }

    @Test
    void updateUser() throws ValidationException {

        User user1 = new User("max22", "Max", "sergeev@max.uzu",
                LocalDate.of(2011, 6, 6));
        User user2 = new User("ira09", "Ira", "avdeeva@ira.ogo",
                LocalDate.of(1978, 2, 12));

        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        userController.getAllUsers().size();

        user2.setId(1L);
        userController.updateUser(user2);

        Assertions.assertEquals(1, userController.getAllUsers().size(), "Wrong size");
        Assertions.assertEquals("Ira", userController.getAllUsers().get(0).getName(),
                "Wrong name");
    }

    @Test
    void addUserWithNegativeIdValue() throws ValidationException {
        User user = new User("Sergey_O", "Serega", "sergey@goodguy.moc",
                LocalDate.of(2010, 1, 1));

        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        user.setId(-1L);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.addUser(user);
        });
        Assertions.assertEquals("Пользователь не найден.", thrown.getMessage());
        Assertions.assertEquals(0, userController.getAllUsers().size(), "Wrong size!");

    }

    @Test
    void findUserByIdWhenUserWithThisIdExists() throws ValidationException {
        User user1 = new User("max22", "Max", "sergeev@max.uzu",
                LocalDate.of(2011, 6, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        Assertions.assertEquals(user1, userController.userById(1L), "Wrong user");
    }

    @Test
    void findUserByIdWhenIdIsNegative() throws ValidationException {
        User user1 = new User("max22", "Max", "sergeev@max.uzu",
                LocalDate.of(2011, 6, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.userById(-1L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void findUserByIdWhenUserWithThisIdNotExists() throws ValidationException {
        User user1 = new User("max22", "Max", "sergeev@max.uzu",
                LocalDate.of(2011, 6, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.userById(9L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void getListOfFriendsWhenUserDontHaveFriends() throws ValidationException {
        User user1 = new User("max22", "Max", "sergeev@max.uzu",
                LocalDate.of(2011, 6, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        Assertions.assertEquals(0, userController.listOfFriends(user1.getId()).size(),
                "Wrong list size");
    }

    @Test
    void getListOfFriendsWhenUserHaveTwoFriends() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        User user3 = new User("max3", "Max3", "sergeev3@max.uzu",
                LocalDate.of(2016, 2, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        user1.getFriendsList().add(2L);
        user1.getFriendsList().add(3L);
        userController.addUser(user1);

        user2.getFriendsList().add(1L);
        userController.addUser(user2);

        user3.getFriendsList().add(1L);
        userController.addUser(user3);

        Assertions.assertEquals(2, userController.listOfFriends(1L).size(),
                "Wrong list size");
        Assertions.assertEquals(user2, userController.listOfFriends(1L).get(0),
                "Wrong user");
        Assertions.assertEquals(user3, userController.listOfFriends(1L).get(1),
                "Wrong user");
    }

    @Test
    void getListOfFriendsWhenUserIdIsNegative() throws ValidationException {
        User user1 = new User("max22", "Max", "sergeev@max.uzu",
                LocalDate.of(2011, 6, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.listOfFriends(-3L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void getListOfFriendsWhenUserWithCurrentIdIsNotExist() throws ValidationException {
        User user1 = new User("max22", "Max", "sergeev@max.uzu",
                LocalDate.of(2011, 6, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.listOfFriends(9L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void getListOfCommonFriendsWhenUsersHaveCommonFriends() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        User user3 = new User("max3", "Max3", "sergeev3@max.uzu",
                LocalDate.of(2016, 2, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        user1.getFriendsList().add(2L);
        user1.getFriendsList().add(3L);
        userController.addUser(user1);

        user2.getFriendsList().add(1L);
        userController.addUser(user2);

        user3.getFriendsList().add(1L);
        userController.addUser(user3);

        Assertions.assertEquals(1, userController.listOfCommonFriends(2L, 3L).size(),
                "Wrong list size");
        Assertions.assertEquals(user1, userController.listOfCommonFriends(2L, 3L).get(0),
                "Wrong user");
    }

    @Test
    void getListOfCommonFriendsWhenUserIdIsNotExist() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        User user3 = new User("max3", "Max3", "sergeev3@max.uzu",
                LocalDate.of(2016, 2, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        user1.getFriendsList().add(2L);
        user1.getFriendsList().add(3L);
        userController.addUser(user1);

        user2.getFriendsList().add(1L);
        userController.addUser(user2);

        user3.getFriendsList().add(1L);
        userController.addUser(user3);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.listOfCommonFriends(9L, 2L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void getListOfCommonFriendsWhenFriendsIdIsNotExist() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        User user3 = new User("max3", "Max3", "sergeev3@max.uzu",
                LocalDate.of(2016, 2, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        user1.getFriendsList().add(2L);
        user1.getFriendsList().add(3L);
        userController.addUser(user1);

        user2.getFriendsList().add(1L);
        userController.addUser(user2);

        user3.getFriendsList().add(1L);
        userController.addUser(user3);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.listOfCommonFriends(2L, 50L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void getListOfCommonFriendsWhenFriendsIdIsNegative() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        User user3 = new User("max3", "Max3", "sergeev3@max.uzu",
                LocalDate.of(2016, 2, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        user1.getFriendsList().add(2L);
        user1.getFriendsList().add(3L);
        userController.addUser(user1);

        user2.getFriendsList().add(1L);
        userController.addUser(user2);

        user3.getFriendsList().add(1L);
        userController.addUser(user3);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.listOfCommonFriends(2L, -50L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void getListOfCommonFriendsWhenUsersIdIsNegative() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        User user3 = new User("max3", "Max3", "sergeev3@max.uzu",
                LocalDate.of(2016, 2, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        user1.getFriendsList().add(2L);
        user1.getFriendsList().add(3L);
        userController.addUser(user1);

        user2.getFriendsList().add(1L);
        userController.addUser(user2);

        user3.getFriendsList().add(1L);
        userController.addUser(user3);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.listOfCommonFriends(-50L, 3L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void getListOfCommonFriendsWhenIdsNotExist() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        User user3 = new User("max3", "Max3", "sergeev3@max.uzu",
                LocalDate.of(2016, 2, 6));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        user1.getFriendsList().add(2L);
        user1.getFriendsList().add(3L);
        userController.addUser(user1);

        user2.getFriendsList().add(1L);
        userController.addUser(user2);

        user3.getFriendsList().add(1L);
        userController.addUser(user3);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.listOfCommonFriends(50L, 133L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void addFriendWhenUserAndFriendAreExist() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(1L, 2L);
        Assertions.assertEquals(1, userController.userById(1L).getFriendsList().size(),
                "Wrong size");
        Assertions.assertTrue(userController.userById(1L).getFriendsList().contains(2L), "Not TRUE");
        Assertions.assertTrue(userController.userById(2L).getFriendsList().contains(1L), "Not TRUE");
    }

    @Test
    void addFriendWhenUserExistAndFriendNot() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        userController.addUser(user2);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.addFriend(1L, 133L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void addFriendWhenBothIdsNotExist() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        userController.addUser(user2);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.addFriend(50L, 133L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void addFriendWhenUsersIdIsNegative() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        userController.addUser(user2);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.addFriend(-50L, 2L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void addFriendWhenFriendsIdIsNegative() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        userController.addUser(user2);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.addFriend(1L, -60L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void removeFriendWhenUserAndFriendAreExist() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(1L, 2L);

        Assertions.assertEquals(1, userController.userById(1L).getFriendsList().size(),
                "Wrong size");
        Assertions.assertEquals(1, userController.userById(2L).getFriendsList().size(),
                "Wrong size");

        userController.removeFriend(1L, 2L);

        Assertions.assertEquals(0, userController.userById(1L).getFriendsList().size(),
                "Wrong size");
        Assertions.assertEquals(0, userController.userById(2L).getFriendsList().size(),
                "Wrong size");
    }

    @Test
    void removeFriendWhenUserIdIsValidAndFriendsIdNotExist() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(1L, 2L);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.removeFriend(1L, 123L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void removeFriendWhenUserIdNotExistAndUserIdIsValid() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(1L, 2L);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.removeFriend(555L, 2L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void removeFriendWhenUsersIdIsNegative() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(1L, 2L);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.removeFriend(-555L, 2L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void removeFriendWhenFriendsIdIsNegative() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        User user2 = new User("max2", "Max2", "sergeev2@max.uzu",
                LocalDate.of(2014, 1, 26));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(1L, 2L);

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.removeFriend(1L, -567L);
        });
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void removeUserWithExistingId() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        Assertions.assertEquals(1, userController.getAllUsers().size(), "Wrong size");
        Assertions.assertEquals(user1, userController.getAllUsers().get(0), "Wrong user");
        userController.removeUser(1L);
        Assertions.assertEquals(0, userController.getAllUsers().size(), "Wrong size");
    }

    @Test
    void removeUserWithNotExistingId() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        Assertions.assertEquals(1, userController.getAllUsers().size(), "Wrong size");
        Assertions.assertEquals(user1, userController.getAllUsers().get(0), "Wrong user");

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.removeUser(674L);
        });
        Assertions.assertEquals("Пользователь не найден.", thrown.getMessage());
    }

    @Test
    void removeUserWithNegativeId() throws ValidationException {
        User user1 = new User("max1", "Max1", "sergeev1@max.uzu",
                LocalDate.of(2011, 6, 16));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.addUser(user1);
        Assertions.assertEquals(1, userController.getAllUsers().size(), "Wrong size");
        Assertions.assertEquals(user1, userController.getAllUsers().get(0), "Wrong user");

        UserNotFoundException thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userController.removeUser(-1L);
        });
        Assertions.assertEquals("Пользователь не найден.", thrown.getMessage());
    }


    @Test
    void getAllFilms() {
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new UserService(new InMemoryUserStorage())));
        Assertions.assertEquals(0, filmController.getAllFilms().size(), "Wrong size");
    }

    @Test
    void addNewFilmWithAllGoodParameters() throws ValidationException {
        Film film = new Film("Superfilm", "Superfilm description",
                LocalDate.of(2000, 12, 12), 120);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new UserService(new InMemoryUserStorage())));
        filmController.addFilm(film);
        Assertions.assertEquals(1, filmController.getAllFilms().size(), "Wrong size");
        Assertions.assertEquals(film, filmController.getAllFilms().get(0), "Wrong user");
    }

    @Test
    void addNewFilmWithEmptyNameField() throws ValidationException {
        Film film = new Film("", "Film2 description",
                LocalDate.of(2012, 3, 2), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new UserService(new InMemoryUserStorage())));
        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Не указано название фильма", thrown.getMessage());
        Assertions.assertEquals(0, filmController.getAllFilms().size(), "Wrong size");
    }

    @Test
    void addNewFilmWithDescriptionFieldLengthOver200Symbols() throws ValidationException {
        Film film = new Film("Film", "1234567891012345678910123456789101234567891012345678910" +
                "1234567891012345678910123456789101234567891012345678910123456789101234567891012345678910" +
                "1234567891012345678910123456789101234567891012345678910123456789101234567891012345678910",
                LocalDate.of(2012, 3, 2), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new UserService(new InMemoryUserStorage())));
        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Описание фильма содержит более 200 символов.", thrown.getMessage());
        Assertions.assertEquals(0, filmController.getAllFilms().size(), "Wrong size");
    }

    @Test
    void addNewFilmWithReleaseDateErlierThan28December1985() throws ValidationException {
        Film film = new Film("Film", "film_description",
                LocalDate.of(1890, 1, 1), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new UserService(new InMemoryUserStorage())));
        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Некорректная дата релиза фильма", thrown.getMessage());
        Assertions.assertEquals(0, filmController.getAllFilms().size(), "Wrong size");
    }

    @Test
    void addNewFilmWithNegativeDuration() throws ValidationException {
        Film film = new Film("Film", "film_description",
                LocalDate.of(1990, 1, 1), -90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new UserService(new InMemoryUserStorage())));
        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Assertions.assertEquals("Некорректно задана продолжительность фильма", thrown.getMessage());
        Assertions.assertEquals(0, filmController.getAllFilms().size(), "Wrong size");
    }

    @Test
    void updateFilm() throws ValidationException {

        Film film1 = new Film("Film", "film_description",
                LocalDate.of(1999, 4, 5), 90);
        ;
        Film film2 = new Film("Movie", "film_description",
                LocalDate.of(1993, 1, 1), 120);

        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new UserService(new InMemoryUserStorage())));
        filmController.addFilm(film1);
        filmController.getAllFilms().size();

        film2.setId(1L);
        filmController.updateFilm(film2);

        Assertions.assertEquals(1, filmController.getAllFilms().size(), "Wrong size");
        Assertions.assertEquals("Movie", filmController.getAllFilms().get(0).getName(),
                "Wrong name");
    }
}
