package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate THE_BORN_OF_CINEMATOGRAPHY = LocalDate.of(1895, 12, 8);
    private static Integer filmIdCounter = 0;
    private Map<Integer, Film> films = new HashMap<>();

    public static void setFilmIdCounter(Integer filmIdCounter) {
        FilmController.filmIdCounter = filmIdCounter;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        List<Film> filmList = new ArrayList<Film>();
        filmList.addAll(films.values());
        return filmList;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            log.info("ValidationException: не указано название фильма");
            throw new ValidationException("Не указано название фильма");
        } else if (film.getDescription().length() > 200) {
            log.info("ValidationException: описание фильма содержит более 200 символов.");
            throw new ValidationException("Описание фильма содержит более 200 символов.");
        } else if (film.getReleaseDate().isBefore(THE_BORN_OF_CINEMATOGRAPHY)) {
            log.info("ValidationException: некорректная дата релиза фильма");
            throw new ValidationException("Некорректная дата релиза фильма");
        } else if (film.getDuration() <= 0) {
            log.info("ValidationException: некорректно задана продолжительность фильма");
            throw new ValidationException("Некорректно задана продолжительность фильма");
        } else {
            film.setId(++filmIdCounter);
            films.put(film.getId(), film);
        }
        return film;
    }


    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            log.info("ValidationException: не указано название фильма");
            throw new ValidationException("Не указано название фильма");
        } else if (film.getDescription().length() > 200) {
            log.info("ValidationException: описание фильма содержит более 200 символов.");
            throw new ValidationException("Описание фильма содержит более 200 символов.");
        } else if (film.getReleaseDate().isBefore(THE_BORN_OF_CINEMATOGRAPHY)) {
            log.info("ValidationException: некорректная дата релиза фильма");
            throw new ValidationException("Некорректная дата релиза фильма");
        } else if (film.getDuration() <= 0) {
            log.info("ValidationException: некорректно задана продолжительность фильма");
            throw new ValidationException("Некорректно задана продолжительность фильма");
        } else if (film.getId() < 0) {
            log.info("в поле user.name записано значение поля user.login");
            throw new ValidationException("Передан отрицательный id");
        } else {
            if (film.getId() != 0 && films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                log.info("Добавлен фильм: {} ", film.getName());
            } else {
                film.setId(++filmIdCounter);
                films.put(film.getId(), film);
                log.info("Добавлен фильм: {} ", film.getName());
            }
        }
        return film;
    }

}
