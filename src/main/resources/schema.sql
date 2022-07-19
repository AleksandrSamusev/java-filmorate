CREATE TABLE IF NOT EXISTS mpa_ratings
(
    id  int generated by default as identity primary key,
    name varchar(5)
    );

CREATE TABLE IF NOT EXISTS films
(
    film_id      int generated by default as identity primary key,
    name         varchar(50),
    description  varchar,
    release_date date,
    duration     INTEGER,
    mpa_rating_id   INTEGER,
    rate         INTEGER,
    FOREIGN KEY (mpa_rating_id)
    REFERENCES mpa_ratings (id)
);


CREATE TABLE IF NOT EXISTS users
(
    user_id  int generated by default as identity primary key,
    login    varchar(30) NOT NULL,
    name     varchar(30) NOT NULL,
    email    varchar(30) NOT NULL,
    birthday date
);

CREATE TABLE IF NOT EXISTS users_liked_films
(
    user_id int,
    film_id int,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS friendship
(
    user_id      int,
    friend_id    int,
    is_confirmed boolean,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id)
        REFERENCES users (user_id),
    FOREIGN KEY (friend_id)
        REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id int generated by default as identity primary key,
    name     varchar(14)
);

CREATE TABLE IF NOT EXISTS films_genres
(
    film_id  int,
    genre_id int,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id),
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id  BIGINT generated by default as identity primary key,
    content VARCHAR,
    is_positive BOOLEAN,
    film_id BIGINT ,
    useful INTEGER,
    FOREIGN KEY (film_id) REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS users_reviews
(
    user_id  BIGINT,
    review_id  BIGINT,
    is_helpful BOOLEAN,
    PRIMARY KEY (user_id, review_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (review_id) REFERENCES reviews (review_id)
);

