package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    static Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM MPA;";
        return jdbcTemplate.query(sql, MpaDbStorage::makeMpa);
    }

    @Override
    public Mpa getMpa(Integer id) {
        if (id > 5) {
            throw new NotFoundException("MPA не найден");
        }
        String sql = "SELECT * FROM MPA WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, MpaDbStorage::makeMpa, id);
    }

}
