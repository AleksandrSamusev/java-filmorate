package ru.yandex.practicum.filmorate.model;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
public class Film {
    private Long id;
    private Integer rate;
    @NonNull
    private String name;
    private MpaRating mpa;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private List<Genre> genres;

    public Film(Integer rate, @NonNull String name, MpaRating mpa, String description,
                LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.rate = rate;

    }

    public Film(Long id, Integer rate, @NonNull String name, MpaRating mpa,
                String description,
                LocalDate releaseDate, Integer duration, List<Genre> genres) {
        this.id = id;
        this.rate = rate;
        this.name = name;
        this.mpa = mpa;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;

    }

    public Film() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MpaRating getMpa() {
        return mpa;
    }

    public void setMpa(MpaRating mpa) {
        this.mpa = mpa;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id) && Objects.equals(rate, film.rate) && name.equals(film.name) && Objects.equals(mpa, film.mpa) && Objects.equals(description, film.description) && Objects.equals(releaseDate, film.releaseDate) && Objects.equals(duration, film.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rate, name, mpa, description, releaseDate, duration);
    }
}
