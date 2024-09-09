package ru.yandex.practicum.filmorate.service;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmServiceInt {
    ResponseEntity<Film> createFilm(Film film);

    List<Film> getFilmsList();

    ResponseEntity<Film> updateFilm(Film film);

    ResponseEntity<String> likeFilm(int id, int userId);

    ResponseEntity<String> removeLike(int id, int userId);

    List<Film> getPopularFilms(Integer count);
}
