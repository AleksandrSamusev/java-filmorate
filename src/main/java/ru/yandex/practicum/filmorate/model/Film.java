package ru.yandex.practicum.filmorate.model;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import java.time.LocalDate;
import java.util.Objects;

@Data
@Builder
public class Film {
    private Long id;
    private Integer rate;
    @NonNull
    private String name;
    private Integer mpaRatingId;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    public Film(Integer rate, @NonNull String name, Integer mpaRatingId, String description,
                LocalDate releaseDate, Integer duration) {
        this.rate = rate;
        this.name = name;
        this.mpaRatingId = mpaRatingId;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Long id, Integer rate, @NonNull String name, Integer mpaRatingId,
                String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.rate = rate;
        this.name = name;
        this.mpaRatingId = mpaRatingId;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id) && Objects.equals(rate, film.rate) && name.equals(film.name) && mpaRatingId.equals(film.mpaRatingId) && Objects.equals(description, film.description) && releaseDate.equals(film.releaseDate) && duration.equals(film.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rate, name, mpaRatingId, description, releaseDate, duration);
    }
}
