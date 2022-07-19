package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class ReviewDaoService {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewDbStorage reviewDbStorage;

    private static final String QUERY_GET_REVIEW_BY_ID = "SELECT * FROM reviews WHERE review_id = ?";

    private static final String QUERY_GET_TEN_REVIEW_SORTED = "SELECT * FROM reviews ORDER BY useful DESC LIMIT 10";

    private static final String QUERY_GET_REVIEW_SORTED = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";

    @Autowired
    public ReviewDaoService(JdbcTemplate jdbcTemplate, ReviewDbStorage reviewDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.reviewDbStorage = reviewDbStorage;
    }

    public Review getReviewById(Long reviewId) {
        if (reviewId < 0) {
            log.info("ReviewNotFoundException: Ревью c id = \"{}\" не найдено", reviewId);
            throw new ReviewNotFoundException("Ревью не найдено");
        }
        log.info("Получено ревью c id = \"{}\"", reviewId);
        return jdbcTemplate.queryForObject(QUERY_GET_REVIEW_BY_ID, this::mapRowToReview, reviewId);
    }

    public Review addReview(Review review) throws ValidationException {
        return reviewDbStorage.addReview(review);
    }

    public Review updateReview(Review review) throws ValidationException {
        return reviewDbStorage.updateReview(review);
    }

    public void deleteReview(Long reviewId) {
        reviewDbStorage.deleteReview(reviewId);
    }

    public List<Review> getAllReviews() {
        return reviewDbStorage.getAllReviews();
    }

    public List<Review> getReviewsSorted(Long filmId, int count) {
        if (filmId < 0) {
            log.info("FilmNotFoundException: Фильм c id = \"{}\" не найден", filmId);
            throw new FilmNotFoundException("Фильм не найден");
        } else if (count < 0) {
            log.info("IncorrectParameterException: Параметр count = \"{}\" задан не верно", count);
        } else if (filmId == 0) {
            return reviewDbStorage.getAllReviews();
        } else if (count == 0) {
            return jdbcTemplate.query(QUERY_GET_TEN_REVIEW_SORTED, this::mapRowToReview);
        } else {
            return jdbcTemplate.query(QUERY_GET_REVIEW_SORTED, this::mapRowToReview, count);
        }
        return null;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {

        return Review.builder()
                .reviewId(resultSet.getLong("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .filmId(resultSet.getLong("film_id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }
}
