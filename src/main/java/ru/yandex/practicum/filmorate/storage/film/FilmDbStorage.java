package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.support.Constant;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmDbStorage implements FilmStorage {

    private static final String QUERY_GET_ALL_FILMS = "SELECT * FROM FILMS";

    private static final String QUERY_ADD_FILM = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE," +
            " DURATION, MPA_RATING_ID, RATE)  VALUES (?, ?, ?, ?, ?, ?); ";

    private static final String QUERY_ADD_GENRE= "insert into FILMS_GENRES (film_id, genre_id) VALUES (?, ?)";

    private static final String QUERY_DELETE_GENRE_BY_FILM_ID = "DELETE FROM films_genres WHERE film_id = ?";

    private static final String QUERY_UPDATE_FILM_BY_ID = "UPDATE FILMS SET " +
            " NAME = ?, DESCRIPTION = ?,  RELEASE_DATE = ?," +
            " DURATION = ?, MPA_RATING_ID = ?, RATE = ? " +
            " WHERE FILM_ID = ?";

    private static final String QUERY_GET_MPA_BY_ID = "SELECT * FROM mpa_ratings WHERE id = ?";

    private static final String  QUERY_DELETE_FILM_BY_ID = "DELETE FROM films WHERE film_id = ?";

    private static final String QUERY_GET_GENRES_BY_FILM_ID = "SELECT * FROM genres JOIN FILMS_GENRES FG " +
            "ON GENRES.GENRE_ID = FG.GENRE_ID  WHERE FG.FILM_ID = ? ";
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(QUERY_GET_ALL_FILMS, this::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        if (isValid(film)) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(QUERY_ADD_FILM, new String[]{"FILM_ID"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setInt(5, film.getMpa().getId());
                stmt.setInt(6, film.getRate());
                return stmt;
            }, keyHolder);
            film.setId(keyHolder.getKey().longValue());

            if (film.getGenres() != null) {
                LinkedHashSet<Genre> newSet = new LinkedHashSet<>(film.getGenres());
                film.setGenres(new ArrayList<>(newSet));

                if (newSet.size() > 0) {
                    for (Genre genre : newSet) {
                        jdbcTemplate.update(QUERY_ADD_GENRE, film.getId(), genre.getId());
                    }
                }
            }
            return film;
        }
        log.info("ValidationException: Фильм c id = \"{}\" не найден", film.getId());
        throw new ValidationException("Фильм не прошел валидацию");
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {

        if (film.getId() < 0) {
            throw new FilmNotFoundException("Фильм не найден");
        }
        if (isValid(film)) {

            jdbcTemplate.update(QUERY_UPDATE_FILM_BY_ID
                    , film.getName()
                    , film.getDescription()
                    , film.getReleaseDate()
                    , film.getDuration()
                    , film.getMpa().getId()
                    , film.getRate()
                    , film.getId());
            if (film.getGenres() != null) {

                LinkedHashSet<Genre> newSet = new LinkedHashSet<>(film.getGenres());
                film.setGenres(new ArrayList<>(newSet));

                if (newSet.size() == 0) {
                    jdbcTemplate.update(QUERY_DELETE_GENRE_BY_FILM_ID, film.getId());
                } else {
                    jdbcTemplate.update(QUERY_DELETE_GENRE_BY_FILM_ID, film.getId());
                    for (Genre genre : newSet) {
                        jdbcTemplate.update(QUERY_ADD_GENRE, film.getId(), genre.getId());
                    }
                }
            }
            return film;
        }
        log.info("FilmNotFoundException: Фильм c id = \"{}\" не найден", film.getId());
        throw new FilmNotFoundException("Фильм не найден");
    }

    @Override
    public void deleteFilm(Long id) {        ;
        jdbcTemplate.update(QUERY_DELETE_FILM_BY_ID, id);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .rate(resultSet.getInt("rate"))
                .name(resultSet.getString("name"))
                .mpa(jdbcTemplate.queryForObject(QUERY_GET_MPA_BY_ID, this::mapRowToMPA,
                        resultSet.getInt("mpa_rating_id")))
                .genres(jdbcTemplate.query(QUERY_GET_GENRES_BY_FILM_ID,
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

    private boolean isValid(Film film) throws ValidationException, FilmNotFoundException {
        if (film.getName().isBlank()) {
            log.info("ValidationException: не указано название фильма");
            throw new ValidationException("Не указано название фильма");
        } else if (film.getDescription().length() > 200) {
            log.info("ValidationException: описание фильма содержит более 200 символов");
            throw new ValidationException("Описание фильма содержит более 200 символов.");
        } else if (film.getReleaseDate().isBefore(Constant.THE_BORN_OF_CINEMATOGRAPHY)) {
            log.info("ValidationException: некорректная дата релиза фильма");
            throw new ValidationException("Некорректная дата релиза фильма");
        } else if (film.getDuration() <= 0) {
            log.info("ValidationException: некорректно задана продолжительность фильма ");
            throw new ValidationException("Некорректно задана продолжительность фильма");
        }
        log.info("Успешная валидация...");
        return true;
    }
}

