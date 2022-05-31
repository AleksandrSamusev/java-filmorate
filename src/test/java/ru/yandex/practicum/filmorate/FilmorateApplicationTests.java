package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {

    @Test
    void contextLoads() {
    }

    @BeforeEach
    @Test
    void resetId() {
        UserController.setUserIdCounter(0);
        FilmController.setFilmIdCounter(0);
    }

    @Test
    void getListOfUsers() {
        UserController userController = new UserController();
        Assertions.assertEquals(0, userController.getAllUsers().size(), "Wrong size");
    }

    @Test
    void addNewUserWithAllGoodParameters() throws ValidationException {
        User user = new User("Niko123", "Nikolas", "niko123@nikolas.lol",
                LocalDate.of(2000, 1, 1));
        UserController userController = new UserController();
        userController.addUser(user);
        Assertions.assertEquals(1, userController.getAllUsers().size(), "Wrong size");
        Assertions.assertEquals(user, userController.getAllUsers().get(0), "Wrong user");
    }

    @Test
    void addNewUserWithEmptyNameField() throws ValidationException {
        User user = new User("Sveta_123", "", "Sveta_123@nikolas.lol",
                LocalDate.of(1965, 12, 12));
        UserController userController = new UserController();
        userController.addUser(user);
        Assertions.assertEquals(1, userController.getAllUsers().size(), "Wrong size");
        Assertions.assertEquals(user.getName(), userController.getAllUsers().get(0).getLogin(),
                "The name and login do not match!");
    }

    @Test
    void addNewUserWithEmptyLoginField() throws ValidationException {
        User user = new User("", "Karlos", "karlos@Omega.lul",
                LocalDate.of(1999, 9, 4));

        UserController userController = new UserController();

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

        UserController userController = new UserController();

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

        UserController userController = new UserController();

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

        UserController userController = new UserController();

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

        UserController userController = new UserController();

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

        UserController userController = new UserController();

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

        UserController userController = new UserController();

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

        UserController userController = new UserController();
        userController.addUser(user1);
        userController.getAllUsers().size();

        user2.setId(1);
        userController.updateUser(user2);

        Assertions.assertEquals(1, userController.getAllUsers().size(), "Wrong size");
        Assertions.assertEquals("Ira", userController.getAllUsers().get(0).getName(),
                "Wrong name");
    }

    @Test
    void addUserWithNegativeIdValue() throws ValidationException {
        User user = new User("Sergey_O", "Serega", "sergey@goodguy.moc",
                LocalDate.of(2010, 1, 1));

        UserController userController = new UserController();
        user.setId(-1);

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        Assertions.assertEquals("Передан отрицательный id", thrown.getMessage());
        Assertions.assertEquals(0, userController.getAllUsers().size(), "Wrong size!");

    }

    @Test
    void getAllFilms() {
        FilmController filmController = new FilmController();
        Assertions.assertEquals(0, filmController.getAllFilms().size(), "Wrong size");
    }

    @Test
    void addNewFilmWithAllGoodParameters() throws ValidationException {
        Film film = new Film("Superfilm", "Superfilm description",
                LocalDate.of(2000, 12, 12), 120);
        FilmController filmController = new FilmController();
        filmController.addFilm(film);
        Assertions.assertEquals(1, filmController.getAllFilms().size(), "Wrong size");
        Assertions.assertEquals(film, filmController.getAllFilms().get(0), "Wrong user");
    }

    @Test
    void addNewFilmWithEmptyNameField() throws ValidationException {
        Film film = new Film("", "Film2 description",
                LocalDate.of(2012, 3, 2), 90);
        FilmController filmController = new FilmController();
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
        FilmController filmController = new FilmController();
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
        FilmController filmController = new FilmController();
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
        FilmController filmController = new FilmController();
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

        FilmController filmController = new FilmController();
        filmController.addFilm(film1);
        filmController.getAllFilms().size();

        film2.setId(1);
        filmController.updateFilm(film2);

        Assertions.assertEquals(1, filmController.getAllFilms().size(), "Wrong size");
        Assertions.assertEquals("Movie", filmController.getAllFilms().get(0).getName(),
                "Wrong name");
    }
}
