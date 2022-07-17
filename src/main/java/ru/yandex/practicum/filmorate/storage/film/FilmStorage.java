package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film addFilm(Film film) throws ValidationException, FilmNotFoundException;

    Film updateFilm(Film film) throws ValidationException, FilmNotFoundException;

    void deleteFilm(Long id);
}
