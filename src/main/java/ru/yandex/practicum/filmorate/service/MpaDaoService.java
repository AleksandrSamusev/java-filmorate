package ru.yandex.practicum.filmorate.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Service
@Slf4j
public class MpaDaoService {
    private final JdbcTemplate jdbcTemplate;

    public MpaDaoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MpaRating getMPAById(Integer id) {
        if (id < 0) {
            log.info("MpaNotFoundException: MPA рейтинг c id = \"{}\" не найден", id);
            throw new MpaNotFoundException("Рейтинг не найден");
        }
        String sqlQuery = "select * from mpa_ratings where id = ?";
        log.info("Вернулся рейтинг c id = \"{}\"", id);
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMPA, id);
    }

    public List<MpaRating> getAllMPA() {
        String sqlQuery = "select * from MPA_RATINGS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMPA);
    }

    private MpaRating mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
