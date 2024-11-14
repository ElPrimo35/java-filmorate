package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService implements FilmServiceInt {
    private final FilmStorage filmStorage;

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
    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    @Override
    public void likeFilm(int id, int userId) {
        filmStorage.likeFilm(id, userId);
    }

    @Override
    public void removeLike(int id, int userId) {
        filmStorage.removeLike(id, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
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
}
