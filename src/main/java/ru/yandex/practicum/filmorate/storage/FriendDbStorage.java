package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@AllArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Integer id, Integer friendId) {
        String sqlAddFriend = "INSERT INTO FRIENDS (userId, friendId, status)" +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlAddFriend, id, friendId, 1);
    }

    @Override
    public void removeFriend(Integer id, Integer friendId) {
        String sqlDeleteFriend = "DELETE FROM FRIENDS \n" +
                "WHERE userId = ? AND friendId = ?;";
        jdbcTemplate.update(sqlDeleteFriend, id, friendId);
    }


    @Override
    public List<User> getMutualFriends(Integer id, Integer otherId) {
        String sql = "select u.ID,\n" +
                "\t   u.EMAIL,\n" +
                "\t   u.LOGIN,\n" +
                "\t   u.NAME,\n" +
                "\t   u.BIRTHDAY \n" +
                "from USERS u, FRIENDS f, FRIENDS o  \n" +
                "where u.ID = f.FRIENDID AND u.ID = o.FRIENDID AND f.USERID = ? AND o.USERID = ?";
        return jdbcTemplate.query(sql, UserDbStorage::makeUser, id, otherId);
    }
}
