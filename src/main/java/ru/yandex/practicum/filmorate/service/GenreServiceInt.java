package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreServiceInt {
    List<Genre> getGenres();

    Genre getGenreById(Integer id);
}
