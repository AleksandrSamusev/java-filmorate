package ru.yandex.practicum.filmorate.storage.genre;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.support.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDbStorage {

    private static final String QUERY_GET_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";

    private static final String QUERY_GET_ALL_GENRES = "SELECT * FROM genres";

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenreById(Integer id) {
        if (id > 0) {
            return jdbcTemplate.queryForObject(QUERY_GET_GENRE_BY_ID, this::mapRowToGenre, id);
        }
        log.info("GenreNotFoundException: жанр с id = \"{}\" не найден", id);
        throw new GenreNotFoundException("Жанр не найден");
    }

    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(QUERY_GET_ALL_GENRES, this::mapRowToGenre);
    }


    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
