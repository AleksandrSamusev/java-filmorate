package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.support.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Service
@Slf4j
public class FilmDaoService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;


    @Autowired
    public FilmDaoService(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
    }

    public List<Film> getMostPopularFilms(int count) {
        return jdbcTemplate.query(Constant.QUERY_MOST_POPULAR_FILMS, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .rate(resultSet.getInt("rate"))
                .name(resultSet.getString("name"))
                .mpa(jdbcTemplate.queryForObject(Constant.QUERY_GET_MPA_BY_ID, this::mapRowToMPA,
                        resultSet.getInt("mpa_rating_id")))
                .genres(jdbcTemplate.query(Constant.QUERY_GET_FILM_GENRES_BY_FILM_ID,
                        this::mapRowToGenre, resultSet.getLong("film_id")))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    private MpaRating mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    public void removeLike(Long filmId, Long userId) {
        validateFilmAndUser(filmId, userId);
        jdbcTemplate.update(Constant.QUERY_DELETE_FILM_LIKE, filmId, userId);
        log.info("Пользователь c id = \"{}\" удалил лайк у фильма c id = \"{}\"", userId, filmId);
    }

    public void giveLike(Long filmId, Long userId) {
        validateFilmAndUser(filmId, userId);
        jdbcTemplate.update(Constant.QUERY_ADD_FILM_LIKE, userId, filmId);
        log.info("Пользователь c id = \"{}\" поставил лайк фильму c id = \"{}\"", userId, filmId);
    }

    private void validateFilmAndUser(Long filmId, Long userId) {
        if (userDbStorage.getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (filmDbStorage.getFilmById(filmId) == null) {
            log.info("FilmNotFoundException: фильм c id = \"{}\" не найден", filmId);
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    public Film addFilm(Film film) throws ValidationException {
        return filmDbStorage.addFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    public Film updateFilm(Film film) throws ValidationException {
        return filmDbStorage.updateFilm(film);
    }

    public void deleteFilm(Long id) {
        filmDbStorage.deleteFilm(id);
    }

    public Film getFilmById(Long id) {
        return filmDbStorage.getFilmById(id);
    }
}
