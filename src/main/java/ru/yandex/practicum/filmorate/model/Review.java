package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.HashSet;

@Data
@Builder
public class Review {

    private Long reviewId;

    @NonNull
    private Long filmId;

    @NonNull
    private String content;

    @NonNull
    private boolean isPositive;

    private HashSet<User> reviewLikes;

    private Integer useful;
}
