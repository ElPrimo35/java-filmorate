package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmServiceInt {
    Film createFilm(Film film);

    List<Film> getFilmsList();

    Film updateFilm(Film film);

    void likeFilm(int id, int userId);

    void removeLike(int id, int userId);

    List<Film> getPopularFilms(Integer count);
}
