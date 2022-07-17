package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class MpaDaoService {

    private static final String QUERY_GET_MPA_BY_ID = "SELECT * FROM mpa_ratings WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;

    public MpaDaoService(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
    }

    public MpaRating getMPAById(Integer id) {
        if (id < 0) {
            log.info("MpaNotFoundException: MPA рейтинг c id = \"{}\" не найден", id);
            throw new MpaNotFoundException("Рейтинг не найден");
        }
        log.info("Вернулся рейтинг c id = \"{}\"", id);
        return jdbcTemplate.queryForObject(QUERY_GET_MPA_BY_ID, this::mapRowToMPA, id);
    }

    private MpaRating mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    public List<MpaRating> getAllMPA() {
        return mpaDbStorage.getAllMPA();
    }
}
