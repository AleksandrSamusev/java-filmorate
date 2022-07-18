package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
public class User {
    private Long id;
    @Email
    @NonNull
    private String email;
    private String login;
    @NonNull
    private String name;
    @NonNull
    private LocalDate birthday;
    private HashSet<Long> friendsList;
}


