package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmDbStorage filmDbStorage;

    @Autowired
    public FilmController(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film receiveFilmById(@PathVariable Long id) {
        return filmDbStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> receiveMostRankedFilms(
            @RequestParam(defaultValue = "10") int count
    ) {
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return filmDbStorage.getMostPopularFilms(count);
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film){
        return filmDbStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmDbStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void giveLike(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        filmDbStorage.giveLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void removeFilm(@PathVariable Long id) {
        filmDbStorage.deleteFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        filmDbStorage.removeLike(id, userId);
    }
}
