package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;


@Data
public class User {
    private Integer id;

    @Email
    @NonNull
    private String email;

    @NonNull
    String login;

    private String name;
    private LocalDate birthday;

    public User(String login, String name, String email, LocalDate birthday) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.id = 0;
    }

}
