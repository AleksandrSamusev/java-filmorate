package ru.yandex.practicum.filmorate.support;

import java.time.LocalDate;

public class Constant {
    public static final LocalDate THE_BORN_OF_CINEMATOGRAPHY = LocalDate.of(1895, 12, 28);

    public static final String QUERY_MOST_POPULAR_FILMS = "SELECT * FROM films AS f" +
            " LEFT JOIN users_liked_films AS ulf ON f.FILM_ID = ulf.FILM_ID" +
            " GROUP BY f.FILM_ID" +
            " ORDER BY Count(user_id) DESC" +
            " LIMIT ?";

    public static final String QUERY_GET_MPA_BY_ID = "SELECT * FROM mpa_ratings WHERE id = ?";

    public static final String QUERY_GET_FILM_GENRES_BY_FILM_ID = "SELECT * FROM genres JOIN FILMS_GENRES FG " +
            "ON GENRES.GENRE_ID = FG.GENRE_ID  WHERE FG.FILM_ID = ? ";

    public static final String QUERY_DELETE_FILM_LIKE = "DELETE FROM users_liked_films " +
            "WHERE film_id = ? AND user_id = ?";

    public static final String QUERY_ADD_FILM_LIKE = "INSERT INTO users_liked_films (" +
            "user_id, film_id) VALUES (?, ?);";

    public static final String QUERY_GET_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";

    public static final String QUERY_GET_ALL_GENRES = "SELECT * FROM genres";

    public static final String QUERY_GET_ALL_MPA = "SELECT * FROM MPA_RATINGS";

    public static final String QUERY_ADD_FRIEND = "insert into friendship (user_id, friend_id, is_confirmed) values (" +
            "?, ?, true);";

    public static final String QUERY_GET_LIST_OF_FRIENDS = "SELECT u.* FROM users AS u" +
            " JOIN friendship AS f ON u.user_id = f.friend_id" +
            " WHERE f.user_id = ?;";

    public static final String QUERY_DELETE_FRIEND = "delete from friendship where user_id = ? and friend_id = ?";

    public static final String QUERY_GET_COMMON_FRIENDS_LIST = "SELECT * FROM users" +
            " WHERE user_id IN (SELECT friend_id FROM friendship" +
            " WHERE user_id = ? AND is_confirmed IS true" +
            " OR user_id = ? AND is_confirmed IS true" +
            " GROUP by friend_id" +
            " HAVING count(friend_id) = 2);";
}
