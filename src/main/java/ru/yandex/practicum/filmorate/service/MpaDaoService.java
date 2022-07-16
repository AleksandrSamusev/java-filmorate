package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

@Service
@Slf4j
public class MpaDaoService {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;

    public MpaDaoService(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
    }

    public MpaRating getMPAById(Integer id) {
        return mpaDbStorage.getMPAById(id);
    }

    public List<MpaRating> getAllMPA() {
        return mpaDbStorage.getAllMPA();
    }
}
