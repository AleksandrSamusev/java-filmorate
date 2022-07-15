package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmDaoService;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmDaoService filmDaoService;

    @Autowired
    public FilmController(FilmDaoService filmDaoService) {
        this.filmDaoService = filmDaoService;
    }


    @GetMapping
    public List<Film> getAllFilms() {
        return filmDaoService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film receiveFilmById(@PathVariable Long id) {
        return filmDaoService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> receiveMostRankedFilms(
            @RequestParam(defaultValue = "10") int count
    ) {
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return filmDaoService.getMostPopularFilms(count);
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        return filmDaoService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        return filmDaoService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void giveLike(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        filmDaoService.giveLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void removeFilm(@PathVariable Long id) {
        filmDaoService.deleteFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        filmDaoService.removeLike(id, userId);
    }
}
