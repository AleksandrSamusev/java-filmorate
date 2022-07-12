package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FilmDbStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Film getFilmById(Long id) {
        String sqlQuery = "select * from films where film_id = ?";
        Film film = this.jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int i) throws SQLException {
                Film film = new Film();
                film.setId(rs.getLong("film_id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                film.setMpaRatingId(rs.getInt("mpa_rating_id"));
                film.setRate(rs.getInt("rate"));
                return film;
            }
        });
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select * from FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into FILMS(NAME, DESCRIPTION, RELEASE_DATE," +
                " DURATION, MPA_RATING_ID, RATE)  values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpaRatingId());
            stmt.setInt(6, film.getRate());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update FILMS set " +
                "name = ?, DESCRIPTION = ?,  RELEASE_DATE = ?," +
                " DURATION = ?, MPA_RATING_ID = ?, RATE = ? " +
                "where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpaRatingId()
                , film.getRate()
                , film.getId());
        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        String sqlQuery = "delete from films where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    public List<Film> getMostPopularFilms(int count) {
        String sqlQuery = "SELECT * FROM films f" +
                "LEFT JOIN users_liked_films AS ulf ON fLEFT.FILM_ID = ulf.FILM_ID" +
                " GROUP BY fLEFT.FILM_ID" +
                " ORDER BY Count(user_id) DESC" +
                " LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .rate(resultSet.getInt("rate"))
                .name(resultSet.getString("name"))
                .mpaRatingId(resultSet.getInt("mpa_rating_id"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
    }

    public void removeLike(Long film_id, Long user_id) {
        String sqlQuery = "delete from users_liked_films where film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, film_id, user_id);
    }

    public void giveLike (Long film_id, Long user_id) {
        String sqlQuery = "INSERT INTO users_liked_films (user_id, film_id) values (?, ?);";
        jdbcTemplate.update(sqlQuery, user_id, film_id);
    }
}

