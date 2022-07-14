package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

@Builder
public class MpaRating {

    Integer id;
    String name;

    public MpaRating(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public MpaRating(Integer id) {
        this.id = id;
    }

    public MpaRating() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
