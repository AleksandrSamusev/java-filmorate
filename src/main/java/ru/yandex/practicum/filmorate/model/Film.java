package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Data
@Builder
public class Film {
    private Long id;
    private Integer rate;
    private HashSet<Long> usersLikes;
    @NonNull
    private String name;
    private MpaRating mpa;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private List<Genre> genres;

}
