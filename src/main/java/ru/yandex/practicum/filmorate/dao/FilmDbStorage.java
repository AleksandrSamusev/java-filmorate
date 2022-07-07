package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Qualifier
public class FilmDbStorage implements FilmStorage {

    private JdbcTemplate jdbcTemplate;

    public FilmDbStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<Film> getAllFilms() {
        return null;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException, FilmNotFoundException {
        return null;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, FilmNotFoundException {
        return null;
    }

    @Override
    public void deleteFilm(Long id) {

    }
}
