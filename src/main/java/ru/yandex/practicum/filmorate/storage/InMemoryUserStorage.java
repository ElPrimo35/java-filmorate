package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @Override
    public User createUser(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User updateUser(User user) {
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
}

