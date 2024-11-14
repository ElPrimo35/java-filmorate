package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;


    private List<Genre> getGenres(Integer filmId) {
        return jdbcTemplate.query("SELECT f3.id, f3.NAME \n" +
                "FROM FILMS f \n" +
                "JOIN FILMGENRES f2 ON f.ID = f2.FILMID \n" +
                "JOIN GENRES f3 ON f2.GENREID = f3.ID \n" +
                "WHERE f2.FILMID = ?", (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }

    private void insertGenreFilm(Integer filmId, Integer genreId) {
        String sqlInsert = "INSERT INTO FILMGENRES (filmId, genreId) VALUES (?, ?)";
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
    public List<Film> getPopularFilms(Integer count) {
        String sql = "SELECT * \n" +
                "FROM FILMS f \n" +
                "WHERE f.ID IN (SELECT FILMID    \n" +
                "FROM FILMS f \n" +
                "JOIN LIKES l ON f.ID = l.FILMID \n" +
                "GROUP BY FILMID\n" +
                "ORDER BY COUNT(FILMID) DESC)\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setGenres(getGenres(film.getId()));
            film.setMpa(getMpa(film.getId()));
            return film;
        }, count);
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
        String sqlDeleteGenres = "DELETE FROM FilmGenres WHERE filmId = ?";

        jdbcTemplate.update(sqlUpdate,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() == null ? null : film.getMpa().getId(),
                film.getId()
        );

        Film film1 = jdbcTemplate.queryForObject("SELECT * FROM FILMS f WHERE id = ?", mapFilm(film.getId()), film.getId());
        assert film1 != null;
        jdbcTemplate.update(sqlDeleteGenres, film1.getId());
        if (film1.getGenres() != null) {
            film1.getGenres().forEach(genre -> insertGenreFilm(film1.getId(), genre.getId()));
        }
        return film1;
    }

    @Override
    public void likeFilm(int id, int userId) {
        String sqlLike = "INSERT INTO LIKES (userId, filmId)\n" +
                "VALUES (?, ?);";
        jdbcTemplate.update(sqlLike, userId, id);
    }

    @Override
    public void removeLike(int id, int userId) {
        String sqlRemoveLike = "DELETE FROM LIKES \n" +
                "WHERE userId = ? AND filmId = ?;";
        jdbcTemplate.update(sqlRemoveLike, userId, id);
    }
}
