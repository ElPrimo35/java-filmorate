package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Primary
@AllArgsConstructor
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private Set<Integer> getUserFriends(Integer userId) {
        Set<Integer> friends = new HashSet<>();
        jdbcTemplate.query("SELECT \"friendId\" \n" +
                "FROM USERFRIENDS u \n" +
                "WHERE \"userId\" = ?", (rs, rowNum) -> {
            do {
                friends.add(rs.getInt("friendId"));
            } while (rs.next());
            return friends;
        }, userId);
        return friends;
    }

    private RowMapper<User> mapUser(Integer userId) {
        return  (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            user.setFriends(getUserFriends(userId));
            return user;
        };
    }

    @Override
    public User createUser(User user) {
        String sqlUser = "INSERT INTO USERS (\"id\", \"email\", \"login\", \"name\", \"birthday\")\n" +
                "VALUES (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sqlUser,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        return jdbcTemplate.queryForObject("SELECT * FROM USERS u WHERE \"id\" = ?", mapUser(user.getId()), user.getId());
    }

    @Override
    public List<User> getUsersList() {
        return jdbcTemplate.query("SELECT * FROM USERS u;", (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            user.setFriends(getUserFriends(user.getId()));
            return user;
        });
    }

    @Override
    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM USERS u WHERE \"id\" = ?", mapUser(id), id));
    }

    @Override
    public User updateUser(User user) {
        String sqlUser = "UPDATE USERS SET \"id\" = ?, \"email\" = ?, " +
                "\"login\" = ?, \"name\" = ?, \"birthday\" = ? " +
                "WHERE \"id\" = ?;";
        jdbcTemplate.update(sqlUser,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return jdbcTemplate.queryForObject("SELECT * FROM USERS u WHERE \"id\" = ?", mapUser(user.getId()), user.getId());
    }
}
