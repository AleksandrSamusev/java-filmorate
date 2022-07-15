package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void giveLike(Long filmId, Long userId) {
        filmAndUserValidation(filmId, userId);
        log.info("Пользователь c id = \"{}\" поставил лайк фильму c id = \"{}\"", userId, filmId);
        filmStorage.getFilmById(filmId).getUsersLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmAndUserValidation(filmId, userId);
        log.info("Пользователь c id = \"{}\" удалил лайк у фильма c id = \"{}\"", userId, filmId);
        filmStorage.getFilmById(filmId).getUsersLikes().remove(userId);
    }

    private void filmAndUserValidation(Long filmId, Long userId) {
        if (userStorage.getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (filmStorage.getFilmById(filmId) == null) {
            log.info("FilmNotFoundException: фильм c id = \"{}\" не найден", filmId);
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    public List<Film> getMostPopularFilms(int count) {
        log.info("вернул список из {} наиболее популярных фильмов", count);
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(o -> -o.getUsersLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) throws ValidationException, FilmNotFoundException {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException, FilmNotFoundException {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Long id) {
        filmStorage.deleteFilm(id);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }
}
