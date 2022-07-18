package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.MpaDaoService;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {

    private final MpaDaoService mpaDaoService;

    @Autowired
    public MpaController(MpaDaoService mpaDaoService) {
        this.mpaDaoService = mpaDaoService;
    }

    @GetMapping()
    public List<MpaRating> getAllMPA() {
        return mpaDaoService.getAllMPA();
    }

    @GetMapping("/{id}")
    public MpaRating getMPAById(@PathVariable Integer id) {
        return mpaDaoService.getMPAById(id);
    }

}
