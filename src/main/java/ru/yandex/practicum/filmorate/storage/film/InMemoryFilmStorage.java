package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.support.Constant;
import ru.yandex.practicum.filmorate.support.FilmIdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> films = new HashMap<>();

    @Override
    public Film getFilmById(Long id) {
        if (id < 0) {
            log.info("FilmNotFoundException: фильм c id = \"{}\" не найден", id);
            throw new FilmNotFoundException("Фильм не найден");
        }
        for (Film film : getAllFilms()) {
            if (film.getId().equals(id)) {
                log.info("вернул фильм c id = \"{}\"", film.getId());
                return film;
            }
        }
        log.info("фильм c id = \"{}\" не найден", id);
        throw new FilmNotFoundException("Фильм не найден");
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> filmList = new ArrayList<>();
        filmList.addAll(films.values());
        return filmList;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException, FilmNotFoundException {
        if (isValid(film) && !films.containsValue(film)) {
            film.setId(FilmIdGenerator.generateId());
            films.put(film.getId(), film);
        }
        log.info("Добавлен фильм, id = \"{}\"", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, FilmNotFoundException {
        if (isValid(film)) {
            if (film.getId() != 0 && films.containsKey(film.getId())) {
                films.put(film.getId(), film);
            } else {
                film.setId(FilmIdGenerator.generateId());
                films.put(film.getId(), film);
            }
        }
        log.info("Фильм с id = \"{}\" обновлен", film.getId());
        return film;
    }

    public void deleteFilm(Long id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм не найден");
        }
        log.info("Фильм с id = \"{}\" удален", id);
        films.remove(id);
    }

    private boolean isValid(Film film) throws ValidationException, FilmNotFoundException {
        if (film.getName().isBlank()) {
            log.info("ValidationException: не указано название фильма");
            throw new ValidationException("Не указано название фильма");
        } else if (film.getDescription().length() > 200) {
            log.info("ValidationException: описание фильма содержит более 200 символов");
            throw new ValidationException("Описание фильма содержит более 200 символов.");
        } else if (film.getReleaseDate().isBefore(Constant.THE_BORN_OF_CINEMATOGRAPHY)) {
            log.info("ValidationException: некорректная дата релиза фильма");
            throw new ValidationException("Некорректная дата релиза фильма");
        } else if (film.getDuration() <= 0) {
            log.info("ValidationException: некорректно задана продолжительность фильма ");
            throw new ValidationException("Некорректно задана продолжительность фильма");
        } else if (film.getId() < 0) {
            log.info("FilmNotFoundException: фильм не найден");
            throw new FilmNotFoundException("Фильм не найден");
        }
        log.info("Успешная валидация...");
        return true;
    }
}
