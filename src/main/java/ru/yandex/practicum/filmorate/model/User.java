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

    @NonNull
    private String login;
    @NonNull
    private String name;
    @NonNull
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

    public User(Long id, String login, String name, String email, LocalDate birthday) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
    }

    public User(Long id, @NonNull String email, @NonNull String login, String name,
                LocalDate birthday, HashSet<Long> friendsList) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friendsList = friendsList;
    }

    public User () {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public HashSet<Long> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(HashSet<Long> friendsList) {
        this.friendsList = friendsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && email.equals(user.email) && login.equals(user.login) && name.equals(user.name) && birthday.equals(user.birthday) && Objects.equals(friendsList, user.friendsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, login, name, birthday, friendsList);
    }
}


