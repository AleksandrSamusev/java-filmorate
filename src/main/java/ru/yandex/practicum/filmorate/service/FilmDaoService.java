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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Service
@Slf4j
public class FilmDaoService {
    private static final String QUERY_MOST_POPULAR_FILMS = "SELECT * FROM films AS f" +
            " LEFT JOIN users_liked_films AS ulf ON f.FILM_ID = ulf.FILM_ID" +
            " GROUP BY f.FILM_ID" +
            " ORDER BY Count(user_id) DESC" +
            " LIMIT ?";

    private static final String QUERY_GET_MPA_BY_ID = "SELECT * FROM mpa_ratings WHERE id = ?";

    private static final String QUERY_GET_FILM_GENRES_BY_FILM_ID = "SELECT * FROM genres JOIN FILMS_GENRES FG " +
            "ON GENRES.GENRE_ID = FG.GENRE_ID  WHERE FG.FILM_ID = ? ";

    private static final String QUERY_DELETE_FILM_LIKE = "DELETE FROM users_liked_films " +
            "WHERE film_id = ? AND user_id = ?";

    private static final String QUERY_ADD_FILM_LIKE = "INSERT INTO users_liked_films (" +
            "user_id, film_id) VALUES (?, ?);";

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;
    private final UserDaoService userDaoService;


    @Autowired
    public FilmDaoService(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage, UserDaoService userDaoService) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
        this.userDaoService = userDaoService;
    }


    public Film getFilmById(Long id) {
        if (id > 0) {
            String sqlQuery = "select * from films where film_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        }
        log.info("FilmNotFoundException: Фильм c id = \"{}\" не найден", id);
        throw new FilmNotFoundException("Фильм не найден");
    }

    public List<Film> getMostPopularFilms(int count) {
        return jdbcTemplate.query(QUERY_MOST_POPULAR_FILMS, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .rate(resultSet.getInt("rate"))
                .name(resultSet.getString("name"))
                .mpa(jdbcTemplate.queryForObject(QUERY_GET_MPA_BY_ID, this::mapRowToMPA,
                        resultSet.getInt("mpa_rating_id")))
                .genres(jdbcTemplate.query(QUERY_GET_FILM_GENRES_BY_FILM_ID,
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
        jdbcTemplate.update(QUERY_DELETE_FILM_LIKE, filmId, userId);
        log.info("Пользователь c id = \"{}\" удалил лайк у фильма c id = \"{}\"", userId, filmId);
    }

    public void giveLike(Long filmId, Long userId) {
        validateFilmAndUser(filmId, userId);
        jdbcTemplate.update(QUERY_ADD_FILM_LIKE, userId, filmId);
        log.info("Пользователь c id = \"{}\" поставил лайк фильму c id = \"{}\"", userId, filmId);
    }

    private void validateFilmAndUser(Long filmId, Long userId) {
        if (userDaoService.getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (getFilmById(filmId) == null) {
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

}
