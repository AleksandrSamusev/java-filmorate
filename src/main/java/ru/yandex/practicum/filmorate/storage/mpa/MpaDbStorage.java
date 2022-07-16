package ru.yandex.practicum.filmorate.storage.mpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.support.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDbStorage {

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MpaRating getMPAById(Integer id) {
        if (id < 0) {
            log.info("MpaNotFoundException: MPA рейтинг c id = \"{}\" не найден", id);
            throw new MpaNotFoundException("Рейтинг не найден");
        }
        log.info("Вернулся рейтинг c id = \"{}\"", id);
        return jdbcTemplate.queryForObject(Constant.QUERY_GET_MPA_BY_ID, this::mapRowToMPA, id);
    }

    public List<MpaRating> getAllMPA() {
        return jdbcTemplate.query(Constant.QUERY_GET_ALL_MPA, this::mapRowToMPA);
    }

    private MpaRating mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
