package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserServiceInt {
    User createUser(User user);

    List<User> getUsersList();

    User updateUser(User user);

    List<User> getFriendsList(int id);

    List<User> getMutualFriends(int id, int otherId);

    User addFriend(int id, int friendId);

    User removeFriend(int id, int friendId);
}
