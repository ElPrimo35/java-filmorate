package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserServiceInt {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

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
    public List<User> getMutualFriends(Integer id, Integer otherId) {
        return friendStorage.getMutualFriends(id, otherId);
    }

    @Override
    public User getUserById(Integer id) {
        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public List<User> getFriendsList(Integer id) {
        userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userStorage.getUserFriends(id);
    }


    @Override
    public User updateUser(User user) {
        log.info("Пришёл запрос на обновление данных пользователя с логином " + user.getLogin());
        User newUser = userStorage.getUserById(user.getId()).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        validate(newUser);
        return userStorage.updateUser(user);
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);
        friendStorage.addFriend(id, friendId);
    }

    @Override
    public void removeFriend(Integer id, Integer friendId) {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);
        friendStorage.removeFriend(id, friendId);
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
