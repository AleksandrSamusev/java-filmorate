package ru.yandex.practicum.filmorate.controller;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {

    private final FilmDbStorage filmDbStorage;

    @Autowired
    public MpaController(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }


    @GetMapping()
    public List<MpaRating> getAllMPA() {
        return filmDbStorage.getAllMPA();
    }

    @GetMapping("/{id}")
    public MpaRating getMPAById(@PathVariable Integer id) {
        return filmDbStorage.getMPAById(id);
    }

}
