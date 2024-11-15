package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT * FROM Genres;";
        return jdbcTemplate.query(sql, GenreDbStorage::makeGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        if (id > 6) {
            throw new NotFoundException("Такого жанра нет");
        }
        String sql = "SELECT * FROM Genres WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, GenreDbStorage::makeGenre, id);
    }
}
