package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.support.Constant;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmDbStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

    public Film getFilmById(Long id) {
        if (id > 0) {
            String sqlQuery = "select * from films where film_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        }
        log.info("FilmNotFoundException: Фильм c id = \"{}\" не найден", id);
        throw new FilmNotFoundException("Фильм не найден");
    }


    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select * from FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        if (isValid(film)) {
            String sqlQuery = "insert into FILMS (NAME, DESCRIPTION, RELEASE_DATE," +
                    " DURATION, MPA_RATING_ID, RATE)  values (?, ?, ?, ?, ?, ?); ";

            String sqlQueryForFilmsGenres = "insert into FILMS_GENRES (film_id, genre_id) VALUES (?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
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
                        jdbcTemplate.update(sqlQueryForFilmsGenres, film.getId(), genre.getId());
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

        String sqlQueryForFilmsGenres = "insert into FILMS_GENRES (film_id, genre_id) VALUES (?, ?)";


        String sqlQueryForDeleteFromFilmsGenres = "delete from films_genres where film_id = ?";

        if (film.getId() < 0) {
            throw new FilmNotFoundException("Фильм не найден");
        }
        if (isValid(film)) {
            String sqlQuery = "update FILMS set " +
                    " NAME = ?, DESCRIPTION = ?,  RELEASE_DATE = ?," +
                    " DURATION = ?, MPA_RATING_ID = ?, RATE = ? " +
                    " where FILM_ID = ?";
            jdbcTemplate.update(sqlQuery
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
                    jdbcTemplate.update(sqlQueryForDeleteFromFilmsGenres, film.getId());
                } else {
                    jdbcTemplate.update(sqlQueryForDeleteFromFilmsGenres, film.getId());
                    for (Genre genre : newSet) {
                        jdbcTemplate.update(sqlQueryForFilmsGenres, film.getId(), genre.getId());
                    }
                }
            }
            return film;
        }
        log.info("FilmNotFoundException: Фильм c id = \"{}\" не найден", film.getId());
        throw new FilmNotFoundException("Фильм не найден");
    }

    @Override
    public void deleteFilm(Long id) {
        if (getFilmById(id) != null) {
            String sqlQuery = "delete from films where film_id = ?";
            jdbcTemplate.update(sqlQuery, id);
        }
    }

    public List<Film> getMostPopularFilms(int count) {
        String sqlQuery = "SELECT * FROM films AS f" +
                " LEFT JOIN users_liked_films AS ulf ON f.FILM_ID = ulf.FILM_ID" +
                " GROUP BY f.FILM_ID" +
                " ORDER BY Count(user_id) DESC" +
                " LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        String sqlQuery = "select * from mpa_ratings where id = ?";
        String sqlQueryForGenre = "select * from genres join FILMS_GENRES FG " +
                "on GENRES.GENRE_ID = FG.GENRE_ID  WHERE FG.FILM_ID = ? ";

        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .rate(resultSet.getInt("rate"))
                .name(resultSet.getString("name"))
                .mpa(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMPA,
                        resultSet.getInt("mpa_rating_id")))
                .genres(jdbcTemplate.query(sqlQueryForGenre,
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
        filmAndUserValidation(filmId, userId);
        String sqlQuery = "delete from users_liked_films where film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.info("Пользователь c id = \"{}\" удалил лайк у фильма c id = \"{}\"", userId, filmId);
    }

    public void giveLike(Long filmId, Long userId) {
        filmAndUserValidation(filmId, userId);
        String sqlQuery = "INSERT INTO users_liked_films (user_id, film_id) values (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, filmId);
        log.info("Пользователь c id = \"{}\" поставил лайк фильму c id = \"{}\"", userId, filmId);
    }

    private void filmAndUserValidation(Long filmId, Long userId) {
        if (userDbStorage.getUserById(userId) == null) {
            log.info("UserNotFoundException: пользователь c id = \"{}\" не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (getFilmById(filmId) == null) {
            log.info("FilmNotFoundException: фильм c id = \"{}\" не найден", filmId);
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    public Genre getGenreById(Integer id) {
        if (id > 0) {
            String sqlQuery = "select * from genres where genre_id = ?";
            Genre genre = this.jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, new RowMapper<Genre>() {
                @Override
                public Genre mapRow(ResultSet rs, int i) throws SQLException {
                    Genre genre = new Genre();
                    genre.setId(rs.getInt("genre_id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                }
            });
            return genre;
        }
        log.info("GenreNotFoundException: жанр с id = \"{}\" не найден", id);
        throw new GenreNotFoundException("Жанр не найден");

    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "select * from genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    public MpaRating getMPAById(Integer id) {
        if (id < 0) {
            log.info("MpaNotFoundException: MPA рейтинг c id = \"{}\" не найден", id);
            throw new MpaNotFoundException("Рейтинг не найден");
        }
        String sql = "select * from mpa_ratings where id = ?";
        MpaRating mpaRating = this.jdbcTemplate.queryForObject(sql, new Object[]{id}, new RowMapper<MpaRating>() {
            @Override
            public MpaRating mapRow(ResultSet rs, int i) throws SQLException {
                MpaRating mpaRating = new MpaRating();
                mpaRating.setId(rs.getInt("id"));
                mpaRating.setName(rs.getString("name"));
                return mpaRating;
            }
        });
        log.info("Вернулся рейтинг c id = \"{}\"", mpaRating.getId());
        return mpaRating;
    }

    public List<MpaRating> getAllMPA() {
        String sqlQuery = "select * from MPA_RATINGS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMPA);
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

