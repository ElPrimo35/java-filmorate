package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film);

    List<Film> getFilmsList();

    public Optional<Film> getFilmById(int id);

    Film updateFilm(Film film);
}
