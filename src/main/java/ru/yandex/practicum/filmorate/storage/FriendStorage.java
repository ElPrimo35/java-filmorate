package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    void addFriend(Integer id, Integer friendId);

    void removeFriend(Integer id, Integer friendId);

    List<User> getMutualFriends(Integer id, Integer otherId);
}
