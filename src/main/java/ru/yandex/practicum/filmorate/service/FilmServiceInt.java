package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmServiceInt {

    List<Genre> getGenres();

    Genre getGenreById(Integer id);

    List<Mpa> getAllMpa();

    Mpa getMpa(Integer id);

    Film createFilm(Film film);

    Optional<Film> getFilmById(Integer id);

    List<Film> getFilmsList();

    Film updateFilm(Film film);

    void likeFilm(int id, int userId);

    void removeLike(int id, int userId);

    List<Film> getPopularFilms(Integer count);
}
