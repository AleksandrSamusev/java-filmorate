package ru.yandex.practicum.filmorate.model;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MpaRating {
    Integer id;
    String name;
}
