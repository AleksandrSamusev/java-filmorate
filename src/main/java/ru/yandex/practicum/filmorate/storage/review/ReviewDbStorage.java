package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY_GET_ALL_REVIEWS = "SELECT * FROM reviews";

    private static final String QUERY_ADD_REVIEW = "INSERT INTO reviews (content, is_positive, film_id, useful) " +
            "VALUES (?, ?, ?, 0);";

    private static final String QUERY_UPDATE_REVIEW_BY_ID = "UPDATE reviews SET " +
            " content = ?, is_Positive = ?,  film_id = ?" +
            " WHERE review_id = ?";

    private static final String QUERY_DELETE_REVIEW_BY_ID = "DELETE FROM reviews WHERE review_id = ?";

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> getAllReviews() {
        return jdbcTemplate.query(QUERY_GET_ALL_REVIEWS, this::mapRowToReview);
    }

    @Override
    public Review addReview(Review review) throws ValidationException {
        if (this.isReviewValid(review)) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(QUERY_ADD_REVIEW, new String[]{"review_id"});
                stmt.setString(1, review.getContent());
                stmt.setBoolean(2, review.isPositive());
                stmt.setLong(3, review.getFilmId());
                return stmt;
            }, keyHolder);

            review.setReviewId(keyHolder.getKey().longValue());
            return review;
        }
        log.info("ValidationException: Ревью c id = \"{}\" не прошло валидацию", review.getReviewId());
        throw new ValidationException("Ревью не прошло валидацию");
    }

    @Override
    public Review updateReview(Review review) throws ValidationException {
        if (review.getReviewId() < 0) {
            log.info("ReviewNotFoundException: Ревью c id = \"{}\" не найдено", review.getReviewId());
            throw new ReviewNotFoundException("Ревью не найдено");
        }
        if (isReviewValid(review)) {
            jdbcTemplate.update(QUERY_UPDATE_REVIEW_BY_ID
                    , review.getContent()
                    , review.isPositive()
                    , review.getFilmId()
                    , review.getReviewId());
        }
        return review;
    }


    @Override
    public void deleteReview(Long reviewId) {
        jdbcTemplate.update(QUERY_DELETE_REVIEW_BY_ID, reviewId);
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

    private boolean isReviewValid(Review review) throws ValidationException, ReviewNotFoundException {
        if (review.getFilmId().toString().isBlank()) {
            log.info("ValidationException: не указан ID фильма");
            throw new ValidationException("не указан ID фильма");
        } else if (review.getContent().length() > 1000) {
            log.info("ValidationException: отзыв содержит более 1000 символов");
            throw new ValidationException("отзыв содержит более 1000 символов");
        } else if (review.getContent().length() == 0) {
            log.info("ValidationException: контент отзыва не может быть пустым");
            throw new ValidationException("контент отзыва не может быть пустым");
        }
        log.info("Успешная валидация...");
        return true;
    }
}
