package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService implements FilmServiceInt {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final Comparator<Film> comparator = (o1, o2) -> Integer.compare(o2.getLikesCount(), o1.getLikesCount());

    @Override
    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("Дата релиза фильма не может быть настолько ранней");
        }
        return filmStorage.createFilm(film);
    }

    @Override
    public void likeFilm(int id, int userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Объект не найден"));
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Объект не найден"));
        film.setLikesCount(addLike(film.getLikesCount()));
        film.getUsersLikedId().add(userId);
    }

    @Override
    public void removeLike(int id, int userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Объект не найден"));
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Объект не найден"));
        film.setLikesCount(removeLike(film.getLikesCount()));
        film.getUsersLikedId().remove(userId);
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
        Film newFilm = filmStorage.getFilmById(film.getId()).orElseThrow(() -> new NotFoundException("Такого фильма нет"));
        newFilm.setName(film.getName());
        newFilm.setDescription(film.getDescription());
        newFilm.setReleaseDate(film.getReleaseDate());
        newFilm.setDuration(film.getDuration());
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
