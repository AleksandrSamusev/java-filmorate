package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class GenreDaoService {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenreById(Integer id) {
        if (id > 0) {
            String sqlQuery = "select * from genres where genre_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        }
        log.info("GenreNotFoundException: жанр с id = \"{}\" не найден", id);
        throw new GenreNotFoundException("Жанр не найден");

    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "select * from genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }


    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
