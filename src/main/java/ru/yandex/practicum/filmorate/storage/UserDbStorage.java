package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Primary
@AllArgsConstructor
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;


    private RowMapper<User> mapUser() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        };
    }

    static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    @Override
    public User createUser(User user) {
        String sqlUser = "INSERT INTO USERS (email, login, name, birthday)\n" +
                "VALUES (?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        Integer userId = (Integer) keyHolder.getKey();


        return jdbcTemplate.queryForObject("SELECT * FROM USERS u WHERE id = ?", mapUser(), userId);
    }

    @Override
    public List<User> getUsersList() {
        return jdbcTemplate.query("SELECT * FROM USERS u;", mapUser());
    }


    @Override
    public List<User> getUserFriends(Integer userId) {
        return jdbcTemplate.query("select u.ID,\n" +
                "\t   u.EMAIL,\n" +
                "\t   u.LOGIN,\n" +
                "\t   u.NAME,\n" +
                "\t   u.BIRTHDAY \n" +
                "from USERS u, FRIENDS f \n" +
                "where u.ID = f.FRIENDID AND f.USERID = ?", mapUser(), userId);
    }


    @Override
    public Optional<User> getUserById(int id) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM USERS u WHERE id = ?", mapUser(), id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public User updateUser(User user) {
        String sqlUser = "UPDATE USERS SET id = ?, email = ?, " +
                "login = ?, name = ?, birthday = ? " +
                "WHERE id = ?;";
        jdbcTemplate.update(sqlUser,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return jdbcTemplate.queryForObject("SELECT * FROM USERS u WHERE id = ?", mapUser(), user.getId());
    }
}
