package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getFilmStorage().getAllFilms();
    }

    @GetMapping("/{id}")
    public Film receiveFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> receiveMostRankedFilms(
            @RequestParam(defaultValue = "10") int count
    ) {
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return filmService.getMostPopularFilms(count);
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        return filmService.getFilmStorage().addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        return filmService.getFilmStorage().updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void giveLike(
            @PathVariable Long id,
            @PathVariable Long userId
    ) throws ValidationException {
        filmService.giveLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void removeFilm(@PathVariable Long id) {
        filmService.getFilmStorage().deleteFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        filmService.removeLike(id, userId);
    }
}
