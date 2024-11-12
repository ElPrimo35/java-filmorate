package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@AllArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;


    private Integer getLikesCount(Integer filmId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(userId) AS likes FROM USERSFILMS u WHERE filmId = "
                + filmId, (rs, rowNum) -> rs.getInt("likes"));
    }

    private Set<Integer> getUsersLiked(Integer filmId) {
        Set<Integer> usersLiked = new HashSet<>();
        jdbcTemplate.query("SELECT userId \n" +
                "FROM USERSFILMS u \n" +
                "WHERE u.filmId = " + filmId, (rs, rowNum) -> {
            do {
                usersLiked.add(rs.getInt("userId"));
            } while (rs.next());
            return usersLiked;
        });
        return usersLiked;
    }

    private List<Genre> getGenres(Integer filmId) {
        return jdbcTemplate.query("SELECT f3.id, f3.NAME \n" +
                "FROM FILMS f \n" +
                "JOIN FILMGENRE f2 ON f.ID = f2.FILMID \n" +
                "JOIN FILMGENRES f3 ON f2.GENREID = f3.ID \n" +
                "WHERE f2.FILMID = ?", (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }

    private void insertGenreFilm(Integer filmId, Integer genreId) {
        String sqlInsert = "INSERT INTO FILMGENRE (filmId, genreId) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlInsert,
                    filmId,
                    genreId
            );
        } catch (DataAccessException ignored) {
        }
    }


    private Mpa getMpa(Integer filmId) {
        Mpa mpa = new Mpa();
        jdbcTemplate.queryForObject("SELECT m.ID,\n" +
                "       m.name \n" +
                "FROM FILMS f \n" +
                "JOIN MPA m ON f.MPA = m.ID \n" +
                "WHERE m.ID = f.MPA AND f.ID = ?", (rs, rowNum) -> {
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        }, filmId);
        return mpa;
    }

    private RowMapper<Film> mapFilm(Integer filmId) {
        return (rs, rowNum) -> {
            Film film1 = new Film();
            film1.setId(rs.getInt("id"));
            film1.setName(rs.getString("name"));
            film1.setDescription(rs.getString("description"));
            film1.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
            film1.setDuration(rs.getInt("duration"));
            film1.setUsersLikedId(getUsersLiked(filmId));
            film1.setLikesCount(getLikesCount(filmId));
            film1.setGenres(getGenres(filmId));
            film1.setMpa(getMpa(filmId));
            return film1;
        };
    }


    @Override
    public Film createFilm(Film film) {

        String sqlFilm = "INSERT INTO FILMS (name, description, releaseDate, " +
                "duration, MPA) " +
                "VALUES (?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlFilm, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() == null ? null : film.getMpa().getId());
            return ps;
        }, keyHolder);
        Integer filmId = (Integer) keyHolder.getKey();

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> insertGenreFilm(filmId, genre.getId()));
        }


        return jdbcTemplate.queryForObject("SELECT * FROM FILMS f WHERE id = ?", mapFilm(filmId), filmId);
    }

    @Override
    public List<Film> getFilmsList() {
        return jdbcTemplate.query("SELECT * FROM FILMS f;", (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setUsersLikedId(getUsersLiked(film.getId()));
            film.setLikesCount(getLikesCount(film.getId()));
            film.setGenres(getGenres(film.getId()));
            film.setMpa(getMpa(film.getId()));
            return film;
        });
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM FILMS f WHERE id = ?", mapFilm(id), id));
    }


    @Override
    public Film updateFilm(Film film) {
        String sqlUpdate = "UPDATE FILMS " +
                "SET name = ?, " +
                "description = ?, releaseDate = ?, duration = ?, MPA = ? " +
                "WHERE id = ?;";
        jdbcTemplate.update(sqlUpdate,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() == null ? null : film.getMpa().getId(),
                film.getId()
        );
        return jdbcTemplate.queryForObject("SELECT * FROM FILMS f WHERE id = ?", mapFilm(film.getId()), film.getId());
    }
}
