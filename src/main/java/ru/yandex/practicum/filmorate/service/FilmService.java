package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService implements FilmServiceInt {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    private final Comparator<Film> comparator = (o1, o2) -> Integer.compare(o2.getLikesCount(), o1.getLikesCount());

    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT * FROM filmGenres;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        });
    }

    @Override
    public Genre getGenreById(Integer id) {
        if (id > 6) {
            throw new NotFoundException("Такого жанра нет");
        }
        String sql = "SELECT * FROM filmGenres WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, id);
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM MPA;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        });
    }

    @Override
    public Mpa getMpa(Integer id) {
        if (id > 5) {
            throw new NotFoundException("MPA не найден");
        }
        String sql = "SELECT * FROM MPA WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        }, id);
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("Дата релиза фильма не может быть настолько ранней");
        }
        if (film.getMpa().getId() > 5) {
            throw new ValidationException("Такого рейтинга нет");
        }
        if (film.getGenres() == null) {
            return filmStorage.createFilm(film);
        }
        for (Genre genre : film.getGenres()) {
            if (genre.getId() > 6) {
                throw new ValidationException("Такого жанра нет");
            }
        }
        return filmStorage.createFilm(film);
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    @Override
    public void likeFilm(int id, int userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Объект не найден"));
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Объект не найден"));
        film.setLikesCount(addLike(film.getLikesCount()));
        film.getUsersLikedId().add(userId);
        String sqlLike = "INSERT INTO USERSFILMS (userId, filmId)\n" +
                "VALUES (?, ?);";
        jdbcTemplate.update(sqlLike, userId, id);
        filmStorage.updateFilm(film);
    }

    @Override
    public void removeLike(int id, int userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Объект не найден"));
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Объект не найден"));
        film.setLikesCount(removeLike(film.getLikesCount()));
        film.getUsersLikedId().remove(userId);
        String sqlLike = "DELETE FROM USERSFILMS \n" +
                "WHERE userId = ? AND filmId = ?;";
        jdbcTemplate.update(sqlLike, userId, id);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getFilmsList().stream()
                .sorted(comparator)
                .limit(count)
                .toList();
    }


    @Override
    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    @Override
    public Film updateFilm(Film film) {
        filmStorage.updateFilm(film);
        return film;
    }

    private int addLike(int likesCount) {
        return ++likesCount;
    }

    private int removeLike(int likesCount) {
        return --likesCount;
    }
}
