package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;

@Data
public class User {
    private Long id;

    @Email
    @NonNull
    private String email;

    @NonNull
    private String login;

    private String name;
    private LocalDate birthday;
    private HashSet<Long> friendsList;

    public User(String login, String name, String email, LocalDate birthday) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.id = 0L;
        this.friendsList = new HashSet<>();
    }

    public HashSet<Long> getFriendsList() {
        return friendsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
