package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class GenreDaoService {

    private static final String QUERY_GET_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;

    public GenreDaoService(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
    }

    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        if (id > 0) {
            return jdbcTemplate.queryForObject(QUERY_GET_GENRE_BY_ID, this::mapRowToGenre, id);
        }
        log.info("GenreNotFoundException: жанр с id = \"{}\" не найден", id);
        throw new GenreNotFoundException("Жанр не найден");
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
