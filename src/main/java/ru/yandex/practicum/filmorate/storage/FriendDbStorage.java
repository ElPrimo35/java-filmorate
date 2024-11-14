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
        String sql = "SELECT *\n" +
                "FROM USERS u \n" +
                "WHERE u.ID IN (SELECT FRIENDID\n" +
                "FROM FRIENDS\n" +
                "WHERE USERID = ?\n" +
                "AND FRIENDID IN (\n" +
                "    SELECT FRIENDID\n" +
                "    FROM FRIENDS\n" +
                "    WHERE USERID = ?\n" +
                "));";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        }, id, otherId);
    }
}
