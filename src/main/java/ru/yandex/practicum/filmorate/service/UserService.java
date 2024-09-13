package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
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
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return user.getFriends().stream().map(friendId -> userStorage.getUserById(friendId).orElseThrow(() -> new NotFoundException("Пользователь не найден"))).toList();
    }

    @Override
    public List<User> getMutualFriends(int id, int otherId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        User user1 = userStorage.getUserById(otherId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        final Set<Integer> friends = user.getFriends();
        final Set<Integer> otherFriends = user1.getFriends();
        return friends.stream()
                .filter(otherFriends::contains)
                .map(userId -> userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден")))
                .collect(Collectors.toList());
    }

    @Override
    public User updateUser(User user) {
        log.info("Пришёл запрос на обновление данных пользователя с логином " + user.getLogin());
        User newUser = userStorage.getUserById(user.getId()).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        newUser.setEmail(user.getEmail());
        newUser.setLogin(user.getLogin());
        newUser.setName(user.getName());
        newUser.setBirthday(user.getBirthday());
        validate(newUser);
        return userStorage.updateUser(newUser);
    }

    @Override
    public User addFriend(int id, int friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Объект не найден"));
        user.getFriends().add(friendId);
        User user1 = userStorage.getUserById(friendId).orElseThrow(() -> new NotFoundException("Объект не найден"));
        user1.getFriends().add(id);
        return user1;
    }

    @Override
    public User removeFriend(int id, int friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Объект не найден"));
        user.getFriends().remove(friendId);
        User user1 = userStorage.getUserById(friendId).orElseThrow(() -> new NotFoundException("Объект не найден"));
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
