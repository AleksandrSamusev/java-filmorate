package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

@Service
@Slf4j
public class GenreDaoService {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;

    public GenreDaoService(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
    }

    public Genre getGenreById(Integer id) {
        return genreDbStorage.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }
}
