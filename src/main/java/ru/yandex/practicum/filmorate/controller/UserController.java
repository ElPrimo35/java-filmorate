package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Пришёл запрос на создание пользователя с логином " + user.getLogin());
        validate(user);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getUsersList() {
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Пришёл запрос на обновление данных пользователя с логином " + user.getLogin());
        validate(user);
        if (users.containsKey(user.getId())) {
            User newUser = users.get(user.getId());
            newUser.setEmail(user.getEmail());
            newUser.setLogin(user.getLogin());
            newUser.setName(user.getName());
            newUser.setBirthday(user.getBirthday());
            users.put(newUser.getId(), newUser);
            return newUser;
        } else {
            throw new ValidationException("Произошла ошибка");
        }

    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ValidationException("Почта пользователься не должна быть пустой");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Почта должна содержать символ '@'");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелов");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
