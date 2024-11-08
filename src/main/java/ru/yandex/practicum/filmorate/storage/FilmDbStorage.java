package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@AllArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;


    private Integer getLikesCount(Integer filmId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(\"userId\") AS likes FROM USERSFILMS u WHERE \"filmId\" = "
                + filmId, (rs, rowNum) -> rs.getInt("likes"));
    }

    private Set<Integer> getUsersLiked(Integer filmId) {
        Set<Integer> usersLiked = new HashSet<>();
        jdbcTemplate.query("SELECT \"userId\" \n" +
                "FROM USERSFILMS u \n" +
                "WHERE u.\"filmId\" = " + filmId, (rs, rowNum) -> {
            do {
                usersLiked.add(rs.getInt("userId"));
            } while (rs.next());
            return usersLiked;
        });
        return usersLiked;
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
            film1.setGenre(rs.getInt("genre"));
            film1.setMpa(rs.getInt("MPA"));
            return film1;
        };
    }

    @Override
    public Film createFilm(Film film) {
        String sqlFilm = "INSERT INTO FILMS (\"id\", \"name\", \"description\", \"releaseDate\", \"duration\", " +
                "\"genre\", \"MPA\")" + " VALUES " +
                "(?, ?, ?, ?, ?, ?, ?);";
        jdbcTemplate.update(sqlFilm,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getGenre(),
                film.getMpa()
        );
        return jdbcTemplate.queryForObject("SELECT * FROM FILMS f WHERE \"id\" = ?", mapFilm(film.getId()), film.getId());
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
            film.setGenre(rs.getInt("genre"));
            film.setMpa(rs.getInt("MPA"));
            return film;
        });
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM FILMS f WHERE \"id\" = ?", mapFilm(id), id));
    }


    @Override
    public Film updateFilm(Film film) {
        String sqlUpdate = "UPDATE FILMS " +
                "SET \"id\" = ?, \"name\" = ?, " +
                "\"description\" = ?, \"releaseDate\" = ?, \"duration\" = ?, \"genre\" = ?, \"MPA\" = ? " +
                "WHERE \"id\" = ?;";
        jdbcTemplate.update(sqlUpdate,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getGenre(),
                film.getMpa(),
                film.getId()
        );
        return jdbcTemplate.queryForObject("SELECT * FROM FILMS f WHERE \"id\" = ?", mapFilm(film.getId()), film.getId());
    }
}
