package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
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
