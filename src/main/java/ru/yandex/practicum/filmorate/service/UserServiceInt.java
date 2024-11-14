package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserServiceInt {
    User createUser(User user);

    List<User> getUsersList();

    User getUserById(Integer id);

    User updateUser(User user);

    List<User> getFriendsList(Integer id);

    List<User> getMutualFriends(Integer id, Integer otherId);

    void addFriend(Integer id, Integer friendId);

    void removeFriend(Integer id, Integer friendId);
}
