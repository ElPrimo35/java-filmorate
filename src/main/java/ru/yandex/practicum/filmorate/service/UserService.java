package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService implements UserServiceInt {
    private final UserStorage userStorage;

    @Override
    public User createUser(User user) {
        log.info("Пришёл запрос на создание пользователя с логином " + user.getLogin());
        validate(user);
        return userStorage.createUser(user);
    }

    @Override
    public List<User> getUsersList() {
        return userStorage.getUsersList();
    }

    @Override
    public List<User> getFriendsList(int id) {
        userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
        User user = userStorage.getUsersList().get(id - 1);
        return user.getFriends().stream().map(friendId -> userStorage.getUsersList().get(friendId - 1)).toList();
    }

    @Override
    public List<User> getMutualFriends(int id, int otherId) {
        User user = userStorage.getUsersList().get(id - 1);
        User user1 = userStorage.getUsersList().get(otherId - 1);
        return user1.getFriends().stream().filter(friendId -> user.getFriends().stream().anyMatch(friend1Id -> friendId == friend1Id)).map(mutualFriendId -> userStorage.getUsersList().get(mutualFriendId - 1)).toList();
    }

    @Override
    public User updateUser(User user) {
        log.info("Пришёл запрос на обновление данных пользователя с логином " + user.getLogin());
        userStorage.getUserById(user.getId()).orElseThrow(() -> new NotFoundException("Пользователь с id: " + user.getId() + " не найден"));
        validate(user);
        return userStorage.updateUser(user);
    }

    @Override
    public User addFriend(int id, int friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
        user.getFriends().add(friendId);
        User user1 = userStorage.getUserById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id: " + friendId + " не найден"));
        user1.getFriends().add(id);
        return user1;
    }

    @Override
    public User removeFriend(int id, int friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
        user.getFriends().remove(friendId);
        User user1 = userStorage.getUserById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id: " + friendId + " не найден"));
        user1.getFriends().remove(id);
        return user1;
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелов");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
