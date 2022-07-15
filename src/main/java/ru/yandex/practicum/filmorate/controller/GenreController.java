package ru.yandex.practicum.filmorate.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.GenreDaoService;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {

    private final GenreDaoService genreDaoService;

    @Autowired
    public GenreController(GenreDaoService genreDaoService) {
        this.genreDaoService = genreDaoService;
    }


    @GetMapping()
    public List<Genre> getAllGenres() {
        return genreDaoService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        return genreDaoService.getGenreById(id);
    }
}
